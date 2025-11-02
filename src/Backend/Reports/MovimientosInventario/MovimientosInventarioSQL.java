package Backend.Reports.MovimientosInventario;

import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class MovimientosInventarioSQL {

    public String run(PGSQLClient pgsqlClient, Integer productoId, String desde, String hasta) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        List<String> rows = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            String sql = "SELECT id, producto_id, usuario_id, tipo_movimiento, cantidad, fecha, motivo " +
                    "FROM movimiento_inventarios WHERE (? IS NULL OR producto_id = ?) AND (fecha::date BETWEEN COALESCE(?::date, '1900-01-01') AND COALESCE(?::date, now()::date)) ORDER BY fecha DESC;";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                if (productoId == null) {
                    ps.setNull(1, java.sql.Types.INTEGER);
                    ps.setNull(2, java.sql.Types.INTEGER);
                } else {
                    ps.setInt(1, productoId);
                    ps.setInt(2, productoId);
                }
                ps.setString(3, (desde == null) ? null : desde);
                ps.setString(4, (hasta == null) ? null : hasta);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long id = rs.getLong("id");
                        int pid = rs.getInt("producto_id");
                        int uid = rs.getInt("usuario_id");
                        String tipo = rs.getString("tipo_movimiento");
                        int cantidad = rs.getInt("cantidad");
                        java.sql.Timestamp fecha = rs.getTimestamp("fecha");
                        String motivo = rs.getString("motivo");
                        rows.add(String.format("%d | producto=%d | usuario=%d | tipo=%s | cantidad=%d | fecha=%s | motivo=%s", id, pid, uid, tipo, cantidad, fecha.toString(), motivo));
                    }
                }
            }
            if (rows.isEmpty()) return "No se encontraron movimientos para los filtros indicados.";
            StringBuilder out = new StringBuilder();
            out.append("Movimientos inventario:\r\n");
            for (String r: rows) out.append(r).append("\r\n");
            return out.toString();
        } catch (Exception e) {
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

}
