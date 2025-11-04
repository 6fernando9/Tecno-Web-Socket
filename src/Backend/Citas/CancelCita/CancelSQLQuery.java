package Backend.Citas.CancelCita;

import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CancelSQLQuery {

    public String cancelarCita(PGSQLClient pgsqlClient, long citaId, Long usuarioId, String motivo) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            connection.setAutoCommit(false);

            // Lock row
            String sel = "SELECT id, cliente_id, estado FROM citas WHERE id = ? FOR UPDATE";
            Long clienteId = null;
            String estado = null;
            try (PreparedStatement ps = connection.prepareStatement(sel)) {
                ps.setLong(1, citaId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        clienteId = rs.getLong("cliente_id");
                        estado = rs.getString("estado");
                    } else {
                        return "Error: cita no encontrada (id=" + citaId + ")";
                    }
                }
            }

            // Authorization: usuarioId must match clienteId
            if (usuarioId == null || !usuarioId.equals(clienteId)) {
                return "Error: usuarioId no autorizado para cancelar esta cita";
            }

            if (estado != null && estado.equalsIgnoreCase("cancelada")) {
                return "Info: la cita ya está cancelada (id=" + citaId + ")";
            }

            String upd = "UPDATE citas SET estado = 'cancelada' WHERE id = ?";
            try (PreparedStatement pu = connection.prepareStatement(upd)) {
                pu.setLong(1, citaId);
                int aff = pu.executeUpdate();
                if (aff == 0) { connection.rollback(); return "Error: no se pudo cancelar la cita"; }
            }

            connection.commit();
            return "OK: cita cancelada (id=" + citaId +")";
        } catch (Exception e) {
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

}
