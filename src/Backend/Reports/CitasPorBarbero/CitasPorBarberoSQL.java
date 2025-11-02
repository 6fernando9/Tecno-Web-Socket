package Backend.Reports.CitasPorBarbero;

import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class CitasPorBarberoSQL {

    public String run(PGSQLClient pgsqlClient, String desde, String hasta) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        List<String> rows = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            String sql = "SELECT u.id AS barbero_id, u.nombre || ' ' || u.apellido AS barbero, COUNT(c.id) AS citas_count " +
                    "FROM citas c JOIN usuarios u ON c.barbero_id = u.id " +
                    "WHERE c.fecha_hora_inicio::date BETWEEN ? AND ? GROUP BY u.id, u.nombre, u.apellido ORDER BY citas_count DESC;";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                Date d1 = (desde == null) ? Date.valueOf("1900-01-01") : Date.valueOf(desde);
                Date d2 = (hasta == null) ? new Date(System.currentTimeMillis()) : Date.valueOf(hasta);
                ps.setDate(1, d1);
                ps.setDate(2, d2);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long id = rs.getLong("barbero_id");
                        String nombre = rs.getString("barbero");
                        int count = rs.getInt("citas_count");
                        rows.add(String.format("%d | %s | citas=%d", id, nombre, count));
                    }
                }
            }
            if (rows.isEmpty()) return "No se encontraron citas en el rango indicado.";
            StringBuilder out = new StringBuilder();
            out.append("Citas por barbero:\r\n");
            for (String r: rows) out.append(r).append("\r\n");
            return out.toString();
        } catch (Exception e) {
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

}
