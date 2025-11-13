package Backend.Citas.CreateCita;

import Backend.Citas.dto.CreateCitaDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class CreateSQLQuery {

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
