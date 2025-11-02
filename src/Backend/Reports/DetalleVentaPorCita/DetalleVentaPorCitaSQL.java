package Backend.Reports.DetalleVentaPorCita;

import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DetalleVentaPorCitaSQL {

    public String run(PGSQLClient pgsqlClient, Integer citaId) {
        if (citaId == null) return "Debe indicar un cita_id válido.";
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        List<String> rows = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            String sql = "SELECT v.id AS venta_id, v.monto_total, v.estado_pago, v.tipo_pago, d.id AS detalle_id, d.producto_id, d.servicio_id, d.cantidad, d.precio_unitario " +
                    "FROM ventas v LEFT JOIN detalles d ON d.venta_id = v.id WHERE v.cita_id = ? ORDER BY v.id, d.id;";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, citaId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int ventaId = rs.getInt("venta_id");
                        double monto = rs.getDouble("monto_total");
                        String estado = rs.getString("estado_pago");
                        String tipo = rs.getString("tipo_pago");
                        Integer detalleId = rs.getObject("detalle_id") != null ? rs.getInt("detalle_id") : null;
                        Integer productoId = rs.getObject("producto_id") != null ? rs.getInt("producto_id") : null;
                        Integer servicioId = rs.getObject("servicio_id") != null ? rs.getInt("servicio_id") : null;
                        Integer cantidad = rs.getObject("cantidad") != null ? rs.getInt("cantidad") : null;
                        Double precio = rs.getObject("precio_unitario") != null ? rs.getDouble("precio_unitario") : null;
                        rows.add(String.format("venta=%d | monto=%.2f | estado=%s | tipo=%s | detalle=%s | producto=%s | servicio=%s | cantidad=%s | precio=%.2f",
                                ventaId, monto, estado, tipo,
                                detalleId==null?"-":detalleId.toString(),
                                productoId==null?"-":productoId.toString(),
                                servicioId==null?"-":servicioId.toString(),
                                cantidad==null?"-":cantidad.toString(),
                                precio==null?0.0:precio));
                    }
                }
            }
            if (rows.isEmpty()) return "No se encontraron ventas asociadas a la cita indicada.";
            StringBuilder out = new StringBuilder();
            out.append("Detalle ventas por cita:\r\n");
            for (String r: rows) out.append(r).append("\r\n");
            return out.toString();
        } catch (Exception e) {
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

}
