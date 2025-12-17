package Backend.Movimientos.ListMovimiento;

import Backend.Movimientos.dto.ListMovimientoDTO;
import Database.PGSQLClient;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ListSQLQuery {

    public String listMovimientos(PGSQLClient pgsqlClient, ListMovimientoDTO dto) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        StringBuilder sql = new StringBuilder("SELECT id, producto_id, tipo_movimiento, cantidad, fecha, motivo, estado FROM movimiento_inventarios WHERE 1=1");
        System.out.println("dto: "+ dto.toString());
        if (dto.productoId != null) sql.append(" AND producto_id = ?");
        if (dto.tipo != null)       sql.append(" AND tipo_movimiento = ?");
        if (dto.desde != null)      sql.append(" AND fecha >= ?");
        if (dto.hasta != null)      sql.append(" AND fecha <= ?");
        if (dto.motivo != null)     sql.append(" AND motivo ILIKE ?");

        sql.append(" ORDER BY fecha DESC");
        System.out.println("SQL HECHO: " + sql);
        List<String> resultados = new ArrayList<>();
        DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        try (Connection con = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                int i = 1;
                if (dto.productoId != null) ps.setLong(i++, dto.productoId);
                if (dto.tipo != null)       ps.setString(i++, dto.tipo);
                if (dto.desde != null)      ps.setTimestamp(i++, Timestamp.valueOf(dto.desde));
                if (dto.hasta != null)      ps.setTimestamp(i++, Timestamp.valueOf(dto.hasta));
                if (dto.motivo != null)     ps.setString(i++, "%" + dto.motivo + "%");

                // 3. Ejecución y procesamiento
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String linea = String.format(
                                "ID: %d | Prod: %d | %s | Cant: %d | Fecha: %s | Estado: %s | Motivo: %s",
                                rs.getLong("id"),
                                rs.getLong("producto_id"),
                                rs.getString("tipo_movimiento").toUpperCase(),
                                rs.getInt("cantidad"),
                                rs.getTimestamp("fecha").toLocalDateTime().format(formatoSalida),
                                rs.getString("estado").toUpperCase(),
                                rs.getString("motivo") != null ? rs.getString("motivo") : "Sin motivo"
                        );
                        resultados.add(linea);
                    }
                }
            }

            // 4. Formateo de la respuesta final
            if (resultados.isEmpty()) {
                return "No se encontraron movimientos con los filtros proporcionados.";
            }

            StringBuilder response = new StringBuilder("--- Listado de Movimientos ---\r\n");
            for (String r : resultados) {
                response.append(r).append("\r\n");
            }
            return response.toString();

        } catch (SQLException e) {
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR INESPERADO: " + e.getMessage();
        }
    }

}
