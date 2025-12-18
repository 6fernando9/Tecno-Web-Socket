package Backend.Citas.UpdateCita;

import Backend.Citas.ConsultarCitas.ConsultarSQL;
import Backend.Citas.ConsultarCitas.ConsultarSQLQuery;
import Backend.Citas.CreateCita.CreateSQLQuery;
import Backend.Citas.GeneralCitaSQLQuery;
import Backend.Citas.dto.CitaDTO;
import Backend.Citas.dto.UpdateCitaDTO;
import Backend.Roles;
import Backend.Servicio.dto.UpdateServicioDTO;
import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.BarberoServicioDTO;
import Database.PGSQLClient;

import java.sql.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static Backend.Citas.CreateCita.CreateSQLQuery.clienteYaTieneCitaEnEsaFecha;

public class UpdateSQLQuery {

    public String updateCita(PGSQLClient pgsqlClient, UpdateCitaDTO dto) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        // SQL para actualizar solo el barbero y la fecha
        String SQL_UPDATE = "UPDATE citas SET barbero_id = ?, fecha = ? WHERE id = ?";
        // SQL para verificar propiedad de la cita
        String SQL_VERIFICAR_DUENO = "SELECT cliente_id, barbero_id FROM citas WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            connection.setAutoCommit(false);

            try {
                // 1. Verificar existencia y roles de los involucrados
                boolean existeCliente = GeneralUsuarioSQLUtils.existeUsuarioConRol(connection, dto.clienteId, Roles.CLIENTE.getDescripcion());
                boolean existeBarbero = GeneralUsuarioSQLUtils.existeUsuarioConRol(connection, dto.barberoId, Roles.BARBERO.getDescripcion());

                if (!existeCliente || !existeBarbero) {
                    return "Error: Uno de los usuarios no tiene el rol correspondiente o no existe.";
                }

                // 2. Verificar que el clienteId del DTO sea el dueño de la cita (Seguridad)
//                try (PreparedStatement psVerif = connection.prepareStatement(SQL_VERIFICAR_DUENO)) {
//                    psVerif.setLong(1, dto.citaId);
//                    try (ResultSet rs = psVerif.executeQuery()) {
//                        if (rs.next()) {
//                            long ownerId = rs.getLong("cliente_id");
//                            if (ownerId != dto.clienteId) {
//                                return "Error: Usted no tiene permiso para modificar esta cita.";
//                            }
//                        } else {
//                            return "Error: La cita con ID " + dto.citaId + " no existe.";
//                        }
//                    }
//                }
                CitaDTO cita = GeneralCitaSQLQuery.findCitaById(connection, dto.citaId);
                if(cita == null){
                    return "Error..Cita no encontrada..";
                }
                BarberoServicioDTO barberoData = GeneralUsuarioSQLUtils.findBarberoConServiciosById(connection, dto.barberoId);

                if (barberoData == null) {
                    return "Error: No se encontró al barbero con ID " + dto.barberoId + " o no tiene servicios registrados.";
                }
                //diferentes barberos
                if(dto.barberoId != cita.barberoId){
                    Set<Long> serviciosIds = cita.servicios.stream().map(servicio -> servicio.id).collect(Collectors.toSet());
                    if (!ConsultarSQLQuery.barberoPoseeTodosLosServicios(barberoData, serviciosIds)) {
                        return "Error: El barbero " + barberoData.nombre + " " + barberoData.apellido +
                                " no ofrece todos los servicios seleccionados para esta cotización.";
                    }
//se asume que si 2 barberos imparten el mismo servicio cobran lo mismo,si quisierammos que no fuese asi entonces en servicioBarbero seria colocar un precio como tal
//                    double montoTotalDeServiciosDeBarberoBd = calcularMontoTotalDeServicios(cita.servicios);
//                    double montoTotalDeServiciosDeBarberoEntrante = barberoData.servicios.stream()
//                            .filter(s -> serviciosIds.contains(s.id))
//                            .mapToDouble(s -> s.precio)
//                            .sum();
//                    if (montoTotalDeServiciosDeBarberoEntrante != montoTotalDeServiciosDeBarberoBd) {
//                        return "No es posible cambiar de barbero dado que no cobran el mismo precio por el total de servicios";
//                    }
                }
                boolean clienteYaTieneCitaConOtroBarbero = clienteYaTieneCitaEnEsaFecha(connection,dto.clienteId,dto.barberoId,dto.fecha);
                if(clienteYaTieneCitaConOtroBarbero){
                    return "Error.. el cliente ya tiene una cita con un barbero en este horario";
                }
                boolean esHorarioDisponible = CreateSQLQuery.esHorarioDisponible(connection, dto.barberoId, dto.fecha);
                if (!esHorarioDisponible) {
                    return "Error: El barbero ya tiene una cita programada en esa fecha y hora.";
                }
                try (PreparedStatement psUpdate = connection.prepareStatement(SQL_UPDATE)) {
                    psUpdate.setLong(1, dto.barberoId);
                    psUpdate.setTimestamp(2, java.sql.Timestamp.valueOf(dto.fecha));
                    psUpdate.setLong(3, dto.citaId);

                    int filasAfectadas = psUpdate.executeUpdate();
                    if (filasAfectadas == 0) {
                        throw new SQLException("No se pudo actualizar la cita.");
                    }
                }

                connection.commit();
                return formatearRespuestaUpdate(dto, barberoData, cita);

            } catch (SQLException e) {
                connection.rollback();
                return "ERROR EN TRANSACCIÓN DE ACTUALIZACIÓN: " + e.getMessage();
            }

        } catch (Exception e) {
            return "ERROR DE CONEXIÓN / BASE DE DATOS: " + e.getMessage();
        }
    }
    public static double calcularMontoTotalDeServicios(Set<UpdateServicioDTO> lista){
        return lista.stream()
                .mapToDouble(s -> s.precio)
                .sum();

    }
    private String formatearRespuestaUpdate(UpdateCitaDTO dto, BarberoServicioDTO barberoData, CitaDTO citaOriginal) {
        StringBuilder sb = new StringBuilder();
        sb.append("🔄 CITA ACTUALIZADA CON ÉXITO\r\n");
        sb.append("------------------------------------------\r\n");
        sb.append(String.format("ID Cita         : %d\r\n", dto.citaId));
        sb.append(String.format("Nueva Fecha/Hora: %s\r\n", dto.fecha.toString()));
        sb.append(String.format("Nuevo Barbero   : %s %s (ID: %d)\r\n",
                barberoData.nombre, barberoData.apellido, barberoData.id));
        sb.append("------------------------------------------\r\n");
        sb.append("Servicios de la Cita:\r\n");

        // Mostramos los servicios que ya tenía la cita (que se mantienen)
        for (UpdateServicioDTO s : citaOriginal.servicios) {
            sb.append(String.format(" - %-20s | Bs. %.2f\r\n", s.nombre, s.precio));
        }

        double montoTotal = calcularMontoTotalDeServicios(citaOriginal.servicios);

        sb.append("------------------------------------------\r\n");
        sb.append(String.format("MONTO TOTAL     : Bs. %.2f\r\n", montoTotal));
        // En actualización, el pago inicial ya fue registrado en la creación
        sb.append(String.format("PAGO INICIAL    : Bs. %.2f\r\n", citaOriginal.pagoInicial));
        sb.append(String.format("SALDO RESTANTE  : Bs. %.2f\r\n", (montoTotal - citaOriginal.pagoInicial)));
        sb.append("==========================================");

        return sb.toString();
    }

}
