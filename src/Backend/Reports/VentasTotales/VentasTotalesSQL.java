package Backend.Reports.VentasTotales;

import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class VentasTotalesSQL {

    public String run(PGSQLClient pgsqlClient, String desde, String hasta) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        List<String> rows = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            String sql = "SELECT date(v.fecha_hora) AS dia, SUM(v.monto_total) AS ingresos_totales, COUNT(v.id) AS ventas_count " +
                    "FROM ventas v WHERE v.fecha_hora::date BETWEEN ? AND ? GROUP BY date(v.fecha_hora) ORDER BY date(v.fecha_hora);";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                Date d1 = (desde == null) ? Date.valueOf("1900-01-01") : Date.valueOf(desde);
                Date d2 = (hasta == null) ? new Date(System.currentTimeMillis()) : Date.valueOf(hasta);
                ps.setDate(1, d1);
                ps.setDate(2, d2);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Date dia = rs.getDate("dia");
                        double ingresos = rs.getDouble("ingresos_totales");
                        int count = rs.getInt("ventas_count");
                        rows.add(String.format("%s | ingresos=%.2f | ventas=%d", dia.toString(), ingresos, count));
                    }
                }
            }
            if (rows.isEmpty()) return "No se encontraron ventas en el rango indicado.";
            StringBuilder out = new StringBuilder();
            out.append("Ventas por dia:\r\n");
            for (String r: rows) out.append(r).append("\r\n");
            return out.toString();
        } catch (Exception e) {
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}
