package Backend.Movimientos.ListMovimiento;

import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ListSQLQuery {

    public String listMovimientos(PGSQLClient pgsqlClient, Long productoId, Long usuarioId, String desde, String hasta, String tipo, String motivo, Integer limit) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        List<String> rows = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");

            StringBuilder sql = new StringBuilder("SELECT id, producto_id, usuario_id, tipo_movimiento, cantidad, fecha, motivo FROM movimiento_inventarios WHERE 1=1");
            if (productoId != null) sql.append(" AND producto_id = ?");
            if (usuarioId != null) sql.append(" AND usuario_id = ?");
            if (tipo != null && !tipo.isBlank()) sql.append(" AND tipo_movimiento = ?");
            if (desde != null && !desde.isBlank()) sql.append(" AND fecha >= ?");
            if (hasta != null && !hasta.isBlank()) sql.append(" AND fecha <= ?");
            if (motivo != null && !motivo.isBlank()) sql.append(" AND motivo ILIKE ?");
            sql.append(" ORDER BY fecha DESC");
            if (limit != null && limit > 0) sql.append(" LIMIT ").append(limit);

            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                int idx = 1;
                if (productoId != null) ps.setLong(idx++, productoId);
                if (usuarioId != null) ps.setLong(idx++, usuarioId);
                if (tipo != null && !tipo.isBlank()) ps.setString(idx++, tipo);
                if (desde != null && !desde.isBlank()) ps.setTimestamp(idx++, Timestamp.valueOf(desde + " 00:00:00"));
                if (hasta != null && !hasta.isBlank()) ps.setTimestamp(idx++, Timestamp.valueOf(hasta + " 23:59:59"));
                if (motivo != null && !motivo.isBlank()) ps.setString(idx++, "%" + motivo + "%");

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long id = rs.getLong("id");
                        long pid = rs.getLong("producto_id");
                        long uid = rs.getLong("usuario_id");
                        String mov = rs.getString("tipo_movimiento");
                        int cantidad = rs.getInt("cantidad");
                        Timestamp fecha = rs.getTimestamp("fecha");
                        String motivo = rs.getString("motivo");
                        rows.add(String.format("%d | producto=%d | usuario=%d | %s | cantidad=%d | fecha=%s | motivo=%s", id, pid, uid, mov, cantidad, fecha, motivo));
                    }
                }
            }

            if (rows.isEmpty()) return "No se encontraron movimientos con los filtros indicados.";
            StringBuilder out = new StringBuilder();
            out.append("Movimientos encontrados:\r\n");
            for (String r : rows) {
                out.append(r).append("\r\n");
            }
            return out.toString();
        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

}
