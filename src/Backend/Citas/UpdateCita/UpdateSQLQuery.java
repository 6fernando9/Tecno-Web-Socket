package Backend.Citas.UpdateCita;

import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class UpdateSQLQuery {

    public String updateCita(PGSQLClient pgsqlClient, long citaId, Long usuarioId, Long barberoId, String fechaInicioISO, String fechaFinISO) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            connection.setAutoCommit(false);

            // Lock the row to avoid races
            String sel = "SELECT id, cliente_id, barbero_id, fecha_hora_inicio, fecha_hora_fin FROM citas WHERE id = ? FOR UPDATE";
            Long clienteId = null;
            Long existingBarbero = null;
            try (PreparedStatement ps = connection.prepareStatement(sel)) {
                ps.setLong(1, citaId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        clienteId = rs.getLong("cliente_id");
                        existingBarbero = rs.getLong("barbero_id");
                    } else {
                        return "Error: cita no encontrada (id=" + citaId + ")";
                    }
                }
            }

            // Authorization: usuarioId must match clienteId (simple rule)
            if (usuarioId == null || !usuarioId.equals(clienteId)) {
                return "Error: usuarioId no autorizado para modificar esta cita";
            }

            // Prepare new timestamps
            Timestamp tsInicio = null;
            Timestamp tsFin = null;
            if (fechaInicioISO != null && !fechaInicioISO.isBlank()) tsInicio = Timestamp.valueOf(fechaInicioISO.replace('T',' ') + ":00");
            if (fechaFinISO != null && !fechaFinISO.isBlank()) tsFin = Timestamp.valueOf(fechaFinISO.replace('T',' ') + ":00");

            Long effectiveBarbero = (barberoId != null) ? barberoId : existingBarbero;

            // Check conflicts for barbero (exclude current cita)
            if (effectiveBarbero != null) {
                String conflictSql = "SELECT COUNT(1) as cnt FROM citas WHERE barbero_id = ? AND id <> ? AND estado <> 'cancelada' AND (fecha_hora_inicio < ? AND fecha_hora_fin > ?)";
                try (PreparedStatement pc = connection.prepareStatement(conflictSql)) {
                    pc.setLong(1, effectiveBarbero);
                    pc.setLong(2, citaId);
                    // Use provided end/start or fallback to +30min
                    Timestamp checkFin = tsFin != null ? tsFin : (tsInicio != null ? new Timestamp(tsInicio.getTime() + 30*60*1000) : new Timestamp(System.currentTimeMillis() + 30*60*1000));
                    Timestamp checkStart = tsInicio != null ? tsInicio : new Timestamp(System.currentTimeMillis());
                    pc.setTimestamp(3, checkFin);
                    pc.setTimestamp(4, checkStart);
                    try (ResultSet rs2 = pc.executeQuery()) {
                        if (rs2.next()) {
                            int cnt = rs2.getInt("cnt");
                            if (cnt > 0) {
                                return "Error: el barbero tiene una cita que se solapa en el rango solicitado.";
                            }
                        }
                    }
                }
            }

            // Build update SQL dynamically
            StringBuilder sb = new StringBuilder("UPDATE citas SET ");
            boolean added = false;
            if (barberoId != null) { sb.append("barbero_id = ?"); added = true; }
            if (tsInicio != null) { if (added) sb.append(", "); sb.append("fecha_hora_inicio = ?"); added = true; }
            if (tsFin != null) { if (added) sb.append(", "); sb.append("fecha_hora_fin = ?"); added = true; }
            if (!added) return "Info: no hay campos para actualizar";
            sb.append(" WHERE id = ?");

            try (PreparedStatement pu = connection.prepareStatement(sb.toString())) {
                int idx = 1;
                if (barberoId != null) pu.setLong(idx++, barberoId);
                if (tsInicio != null) pu.setTimestamp(idx++, tsInicio);
                if (tsFin != null) pu.setTimestamp(idx++, tsFin);
                pu.setLong(idx++, citaId);
                int aff = pu.executeUpdate();
                if (aff == 0) { connection.rollback(); return "Error: no se pudo actualizar la cita"; }
            }

            connection.commit();
            return "OK: cita reprogramada (id=" + citaId + ")";
        } catch (Exception e) {
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}
