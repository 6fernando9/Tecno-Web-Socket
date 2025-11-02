package Backend.Reports.VentasPorProducto;

import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class VentasPorProductoSQL {

    public String run(PGSQLClient pgsqlClient, String desde, String hasta) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        List<String> rows = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            String sql = "SELECT p.id AS producto_id, p.nombre AS producto, COALESCE(SUM(d.cantidad),0) AS unidades_vendidas, COALESCE(SUM(d.cantidad * d.precio_unitario),0) AS total_ingresos " +
                    "FROM detalles d JOIN ventas v ON d.venta_id = v.id JOIN productos p ON d.producto_id = p.id " +
                    "WHERE v.fecha_hora::date BETWEEN ? AND ? GROUP BY p.id, p.nombre ORDER BY unidades_vendidas DESC;";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                Date d1 = (desde == null) ? Date.valueOf("1900-01-01") : Date.valueOf(desde);
                Date d2 = (hasta == null) ? new Date(System.currentTimeMillis()) : Date.valueOf(hasta);
                ps.setDate(1, d1);
                ps.setDate(2, d2);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long id = rs.getLong("producto_id");
                        String nombre = rs.getString("producto");
                        long unidades = rs.getLong("unidades_vendidas");
                        double ingresos = rs.getDouble("total_ingresos");
                        rows.add(String.format("%d | %s | unidades=%d | ingresos=%.2f", id, nombre, unidades, ingresos));
                    }
                }
            }
            if (rows.isEmpty()) return "No se encontraron ventas de productos en el rango indicado.";
            StringBuilder out = new StringBuilder();
            out.append("Ventas por producto:\r\n");
            for (String r: rows) out.append(r).append("\r\n");
            return out.toString();
        } catch (Exception e) {
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

}
