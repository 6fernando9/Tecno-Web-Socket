package Backend.Citas.CreateCita;

import Backend.Citas.dto.CreateCitaDTO;
import Backend.Citas.dto.CreateCitaV2DTO;
import Backend.Pagos.GeneralPagoSQLUtils;
import Backend.Pagos.dto.TipoPagoDTO;
import Backend.Roles;
import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.BarberoServicioDTO;
import Database.PGSQLClient;

import java.sql.*;

public class CreateSQLQuery {

    private static String INSERTAR_CITA = """
            INSERT INTO citas 
            (cliente_id, barbero_id,tipo_pago_id,pago_inicial,monto_total,porcentaje_cita,fecha) VALUES (?,?,?,?,?,?,?)
            RETURNING id
            """;
    private static String INSERTAR_CITA_SERVICIO = """
            INSERT INTO cita_servicios(servicio_id,cita_id) VALUES (?,?)
            """;



    public String executeCreateCitav2(PGSQLClient pgsqlClient, CreateCitaV2DTO dto) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            connection.setAutoCommit(false);
            System.out.println("Iniciando transacción de reserva de cita...");

            try {
                boolean existeBarbero = GeneralUsuarioSQLUtils.existeUsuarioConRol(connection, dto.barberoId, Roles.BARBERO.getDescripcion());
                if(!existeBarbero){
                    return "Error... el usuario no fue encontrado en la tabla barbero..";
                }
                boolean existeCliente = GeneralUsuarioSQLUtils.existeUsuarioConRol(connection, dto.clienteId, Roles.CLIENTE.getDescripcion());
                if(!existeCliente){
                    return "Error... el usuario no fue encontrado en la tabla cliente..";
                }
                BarberoServicioDTO barberoData = GeneralUsuarioSQLUtils.findBarberoConServiciosById(connection, dto.barberoId);
                if (barberoData == null) return "Barbero no encontrado.";

                double montoTotal = barberoData.servicios.stream()
                        .filter(s -> dto.serviciosIds.contains(s.id))
                        .mapToDouble(s -> s.precio)
                        .sum();
                TipoPagoDTO tipoPagoDTO = GeneralPagoSQLUtils.findTipoPagoByName(connection,dto.tipoPago);
                double porcentajeCita = GeneralPagoSQLUtils.findPorcentajeOfConfiguration(connection);
                if(tipoPagoDTO == null){
                    return "Error.. Tipo de pago no encontrado";
                }
                if(porcentajeCita == 0){
                    return "Error.. deberia haber un porcentaje de cita mayor a 0";
                }
                porcentajeCita = porcentajeCita / 100;
                boolean clienteYaTieneCitaConOtroBarbero = clienteYaTieneCitaEnEsaFecha(connection,dto.clienteId,dto.barberoId,dto.fecha);
                if(clienteYaTieneCitaConOtroBarbero){
                    return "Error.. el cliente ya tiene una cita con un barbero en este horario";
                }
                boolean esHorarioDisponible = esHorarioDisponible(connection,dto.barberoId,dto.fecha);
                if (!esHorarioDisponible) {
                    return "El Horario no esta disponible, consulte otro barbero..";
                }
                if(porcentajeCita * montoTotal != dto.pagoInicial){
                    return "El pago inicial siempre debe ser del porcentaje acordado y no mas ni menos!";
                }

                long citaId = -1;
                try (PreparedStatement psCita = connection.prepareStatement(INSERTAR_CITA)) {
                    psCita.setLong(1, dto.clienteId);
                    psCita.setLong(2, dto.barberoId);
                    psCita.setLong(3, tipoPagoDTO.id);
                    psCita.setDouble(4, dto.pagoInicial);
                    psCita.setDouble(5, montoTotal);
                    psCita.setDouble(6, porcentajeCita);
                    psCita.setTimestamp(7, java.sql.Timestamp.valueOf(dto.fecha));

                    try (ResultSet rs = psCita.executeQuery()) {
                        if (rs.next()) {
                            citaId = rs.getLong(1);
                        }
                    }
                }


                try (PreparedStatement psServicios = connection.prepareStatement(INSERTAR_CITA_SERVICIO)) {
                    for (Long servicioId : dto.serviciosIds) {
                        psServicios.setLong(1, servicioId);
                        psServicios.setLong(2, citaId);
                        psServicios.addBatch();
                    }
                    psServicios.executeBatch();
                }

                connection.commit();
                return formatearRespuestaExito(citaId, montoTotal, dto, barberoData);

            } catch (SQLException e) {
                connection.rollback();
                return "ERROR EN TRANSACCIÓN: " + e.getMessage();
            }

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE CONEXIÓN: " + e.getMessage();
        }
    }

    private String formatearRespuestaExito(long citaId, double montoTotal, CreateCitaV2DTO dto, BarberoServicioDTO barberoData) {
        StringBuilder sb = new StringBuilder();
        sb.append("✅ CITA AGENDADA CON ÉXITO\r\n");
        sb.append("------------------------------------------\r\n");
        sb.append(String.format("ID Cita      : %d\r\n", citaId));
        sb.append(String.format("Fecha y Hora : %s\r\n", dto.fecha.toString()));
        sb.append(String.format("Nuevo Barbero   : %s %s (ID: %d)\r\n",
                barberoData.nombre, barberoData.apellido, barberoData.id));
        sb.append("------------------------------------------\r\n");
        sb.append("Servicios Seleccionados:\r\n");

        // Filtramos los servicios que el barbero ofrece y que el usuario eligió
        barberoData.servicios.stream()
                .filter(s -> dto.serviciosIds.contains(s.id))
                .forEach(s -> sb.append(String.format(" - %-20s | Bs. %.2f\r\n", s.nombre, s.precio)));

        sb.append("------------------------------------------\r\n");
        sb.append(String.format("MONTO TOTAL  : Bs. %.2f\r\n", montoTotal));
        sb.append(String.format("PAGO INICIAL : Bs. %.2f (%s)\r\n", dto.pagoInicial, dto.tipoPago));
        sb.append(String.format("SALDO PENDI. : Bs. %.2f\r\n", (montoTotal - dto.pagoInicial)));
        sb.append("==========================================");

        return sb.toString();
    }
    public static boolean esHorarioDisponible(Connection conn, Long barberoId, java.time.LocalDateTime fecha) throws SQLException {
        //si hay alguna pendiente
        String sql = "SELECT COUNT(*) FROM citas WHERE barbero_id = ? AND fecha = ? AND estado IN ('pendiente','confirmada')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, barberoId);
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }
    public static boolean clienteYaTieneCitaEnEsaFecha(Connection conn, Long clienteId,Long barberoId, java.time.LocalDateTime fecha) throws SQLException {
        String sql = "SELECT COUNT(*) FROM citas WHERE cliente_id = ? AND barbero_id != ? AND fecha = ? AND estado IN ('pendiente')";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, clienteId);
            ps.setLong(2,barberoId);
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(fecha));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public String executeCreateCita(PGSQLClient pgsqlClient, CreateCitaDTO dto) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");
            connection.setAutoCommit(false);

            // convert ISO datetime YYYY-MM-DDTHH:MM to Timestam
            Timestamp tsInicio = parseFecha(dto.fechaHoraInicio, false);
            Timestamp tsFin = parseFecha(dto.fechaHoraFin, true);

            // If barbero specified, check overlapping citas (compare with fecha_hora_fin column)
            if (dto.barberoId != null) {
                String conflictSql = "SELECT COUNT(1) as cnt FROM citas WHERE barbero_id = ? AND estado <> 'cancelada' AND (fecha_hora_inicio < ? AND fecha_hora_fin > ?)";
                try (PreparedStatement psC = connection.prepareStatement(conflictSql)) {
                    psC.setLong(1, dto.barberoId);
                    psC.setTimestamp(2, tsFin != null ? tsFin : new Timestamp(tsInicio.getTime() + 3600_000));
                    psC.setTimestamp(3, tsInicio);
                    try (ResultSet rs = psC.executeQuery()) {
                        if (rs.next()) {
                            int cnt = rs.getInt("cnt");
                            if (cnt > 0) {
                                return "Error: el barbero tiene una cita que se solapa en el rango solicitado.";
                            }
                        }
                    }
                }
            }

            // Insert cita
            //            String subject = "cita_create["123","45","1,2","2025-11-03T14:30","2025-11-03T15:00","Corte urgente","10.00"]";
            String insertCita = "INSERT INTO citas (cliente_id, barbero_id, fecha_hora_inicio, fecha_hora_fin, estado, pago_inicial) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
            long citaId;
            try (PreparedStatement ps = connection.prepareStatement(insertCita)) {
                ps.setLong(1, dto.clienteId);
                if (dto.barberoId != null) ps.setLong(2, dto.barberoId); else ps.setNull(2, java.sql.Types.BIGINT);
                ps.setTimestamp(3, tsInicio);
                if (tsFin != null) ps.setTimestamp(4, tsFin); else ps.setTimestamp(4, new Timestamp(tsInicio.getTime() + 30*60*1000));
                ps.setString(5, "pendiente_pago_adelanto");
                if (dto.pagoInicial != null) ps.setDouble(6, dto.pagoInicial); else ps.setNull(6, java.sql.Types.NUMERIC);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        citaId = rs.getLong(1);
                    } else {
                        connection.rollback();
                        return "Error: no se pudo insertar la cita.";
                    }
                }
            }
//            String subject = "cita_create["123","45","1,2","2025-11-03T14:30","2025-11-03T15:00","Corte urgente","10.00"]";
            // Insert cita_servicios (if any)
            if (dto.serviciosCsv != null && !dto.serviciosCsv.isBlank()) {
                String[] servicios = dto.serviciosCsv.split(",");
                String insertCS = "INSERT INTO cita_servicios (cita_id, servicio_id, precio_cobrado) VALUES (?, ?, NULL)";
                try (PreparedStatement ps2 = connection.prepareStatement(insertCS)) {
                    for (String s : servicios) {
                        String trimmed = s.trim();
                        if (trimmed.isEmpty()) continue;
                        long sid = Long.parseLong(trimmed);
                        ps2.setLong(1, citaId);
                        ps2.setLong(2, sid);
                        ps2.addBatch();
                    }
                    ps2.executeBatch();
                }
            }

            connection.commit();
            return "OK: Cita creada con id=" + citaId;
        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
    private Timestamp parseFecha(String f, boolean isHasta) throws Exception {
        if (f == null || f.isBlank()) return null;
        f = f.trim();
        f = f.replace("/", "-");
        // Caso 1: Solo fecha (YYYY-MM-DD)
        if (f.matches("\\d{4}-\\d{2}-\\d{2}")) {
            if (isHasta)
                return Timestamp.valueOf(f + " 23:59:59");
            else
                return Timestamp.valueOf(f + " 00:00:00");
        }

        // Caso 2: ISO con T
        if (f.contains("T"))
            f = f.replace("T", " ");

        // Caso 3: Fecha y hora sin segundos
        if (f.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"))
            f = f + ":00";

        return Timestamp.valueOf(f);
    }

}
