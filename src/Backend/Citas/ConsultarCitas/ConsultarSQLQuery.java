package Backend.Citas.ConsultarCitas;

import Backend.Citas.dto.ConsultarBarberoDTO;
import Backend.Citas.dto.ConsultarServicioBarberoDTO;
import Backend.Pagos.GeneralPagoSQLUtils;
import Backend.Servicio.dto.UpdateServicioDTO;
import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.BarberoServicioDTO;
import Database.PGSQLClient;

import java.sql.*;
import java.util.Set;
import java.util.stream.Collectors;

public class ConsultarSQLQuery {

    private static String CONSULTAR_BARBEROS_EN_FECHA = """
            SELECT u.id, u.name, u.apellido,u.email,u.telefono
            FROM users u
                     JOIN barberos b ON u.id = b.id
            WHERE u.rol = 'barbero'
              AND u.estado = 'activo'
              AND b.estado_barbero = 'disponible'
              AND b.id NOT IN (SELECT barbero_id
                               FROM citas
                               WHERE fecha = ?
                                 AND barbero_id IS NOT NULL
                                 AND estado = 'pendiente')
           """;

    public String executeConsultarBarberosDisponibles(PGSQLClient pgsqlClient, ConsultarBarberoDTO dto) {

        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        StringBuilder response = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");
            try(PreparedStatement ps = connection.prepareStatement(CONSULTAR_BARBEROS_EN_FECHA)){
                ps.setTimestamp(1,Timestamp.valueOf(dto.fecha));
                try(ResultSet rs = ps.executeQuery()){
                    response.append("BARBEROS DISPONIBLES PARA EL ").append(dto.fecha).append("\r\n");
                    response.append("--------------------------------------------------\r\n");
                    int contador = 1;
                    while (rs.next()) {
                        response.append(formatearBarberoDisponible(rs, contador++));
                    }

                    if (contador == 1) {
                        return "Lo sentimos, no se encontraron barberos disponibles para la fecha: " + dto.fecha;
                    }
                    return response.toString();
                }
            }
        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

    public String executeConsultarMontoDelServicioSolicitado(PGSQLClient pgsqlClient, ConsultarServicioBarberoDTO dto){
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database - Consulta de Monto");
            BarberoServicioDTO barberoData = GeneralUsuarioSQLUtils.findBarberoConServiciosById(connection, dto.barberoId);

            if (barberoData == null) {
                return "Error: No se encontró al barbero con ID " + dto.barberoId + " o no tiene servicios registrados.";
            }

            if (!barberoPoseeTodosLosServicios(barberoData, dto.serviciosIds)) {
                return "Error: El barbero " + barberoData.nombre + " " + barberoData.apellido +
                        " no ofrece todos los servicios seleccionados para esta cotización.";
            }

            double montoTotal = 0.0;
            StringBuilder detalleServicios = new StringBuilder();
            int item = 1;

            for (UpdateServicioDTO servicio : barberoData.servicios) {
                if (dto.serviciosIds.contains(String.valueOf(servicio.id))) {
                    montoTotal += servicio.precio;
                    detalleServicios.append(String.format("   %d. %s (Bs. %.2f)\r\n", item++, servicio.nombre, servicio.precio));
                }
            }
            double porcentajeCita = GeneralPagoSQLUtils.findPorcentajeOfConfiguration(connection);
            if(porcentajeCita == 0){
                return "Error.. deberia haber un porcentaje de cita mayor a 0";
            }
            porcentajeCita = porcentajeCita/100;
            StringBuilder response = new StringBuilder();
            response.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\r\n");
            response.append("RESUMEN DE COTIZACIÓN SOLICITADA\r\n");
            response.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\r\n");
            response.append("Barbero: ").append(barberoData.nombre).append(" ").append(barberoData.apellido).append("\r\n");
            response.append("Detalle de servicios:\r\n");
            response.append(detalleServicios);
            response.append("----------------------------------------\r\n");
            response.append(String.format("TOTAL A PAGAR: Bs. %.2f\r\n", montoTotal));
            response.append(String.format("TOTAL A PAGAR CON PORCENTAJE: Bs. %.2f\r\n", montoTotal * porcentajeCita));
            response.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\r\n");

            return response.toString();

        } catch (SQLException e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

    public static boolean barberoPoseeTodosLosServicios(BarberoServicioDTO barberoData, Set<Long> solicitados) {
        Set<Long> idsHabilitados = barberoData.servicios.stream()
                .map(s -> (s.id))
                .collect(Collectors.toSet());

        return idsHabilitados.containsAll(solicitados);
    }
    public double calcularMontoTotalDesdeDTO(BarberoServicioDTO barberoData, Set<String> serviciosSolicitadosIds) {
        double total = 0.0;
        for (UpdateServicioDTO servicio : barberoData.servicios) {
            if (serviciosSolicitadosIds.contains(String.valueOf(servicio.id))) {
                total += servicio.precio;
            }
        }
        return total;
    }
    private String formatearBarberoDisponible(ResultSet rs, int numero) throws SQLException {
        long id = rs.getLong("id");
        String nombre = rs.getString("name");
        String apellido = rs.getString("apellido");
        String email = rs.getString("email");
        String telefono = rs.getString("telefono");

        return String.format(
                "----------------------------------------------------\r\n" +
                        "Opcion %d:\r\n" +
                        "ID Barbero: %d\r\n" +
                        "Nombre: %s %s\r\n" +
                        "Email: %s\r\n" +
                        "Teléfono: %s\r\n" +
                        "----------------------------------------------------\r\n",
                numero, id, nombre, apellido, email, telefono
        );
    }
}
