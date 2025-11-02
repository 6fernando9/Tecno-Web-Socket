package Backend.Productos.ListarProducto;

import Backend.Productos.dto.ComparadorSigno;
import Database.PGSQLClient;

import java.math.BigDecimal;
import java.sql.*;

public class ListarStockActualSQLQuery {
    private static final String LIST_BASE_SELECT =
            "SELECT id, nombre, descripcion, precio_venta, stock_actual, stock_minimo FROM productos";


    public String executeListarProductos(PGSQLClient pgsqlClient, ComparadorSigno comparador) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {

            System.out.println("Connecting successfully to database");

            String sql;
            boolean esComparadorNulo = comparador == null;

            PreparedStatement ps;
            if (esComparadorNulo) {
                // Sin filtro: listar todos
                sql = LIST_BASE_SELECT + " ORDER BY id ASC";
                ps = connection.prepareStatement(sql);
            } else {
                String operador = mapOperadorSeguro(comparador.signo);
                if (operador == null) {
                    return "Error: operador inválido: " + comparador.signo;
                }
                sql = LIST_BASE_SELECT + " WHERE stock_actual " + operador + " ? ORDER BY id ASC";
                ps = connection.prepareStatement(sql);
                ps.setBigDecimal(1, new java.math.BigDecimal(comparador.valor.toString()));
            }

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder result = new StringBuilder();

                while (rs.next()) {
                    result.append(formatearProducto(rs));
                }

                if (result.length() == 0) {
                    return "[]";
                }
                return result.toString();
            }

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
    public String executeListarProductosBetween(PGSQLClient pgsqlClient, int[] intervalo) {
        if (intervalo == null || intervalo.length != 2) {
            return "Error: se esperaban dos valores (mínimo y máximo)";
        }

        int min = intervalo[0];
        int max = intervalo[1];

        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        String sql = LIST_BASE_SELECT +
                " WHERE stock_actual BETWEEN ? AND ? ORDER BY id ASC";

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword());
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, min);
            ps.setInt(2, max);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder result = new StringBuilder();

                while (rs.next()) {
                    result.append(formatearProducto(rs));
                }

                if (result.length() == 0) {
                    return "[]";
                }

                return result.toString();
            }

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

    private String mapOperadorSeguro(String signo) {
        if (signo == null) return null;
        switch (signo) {
            case ">":  return ">";
            case ">=": return ">=";
            case "<":  return "<";
            case "<=": return "<=";
            case "=":
            case "==": return "=";
            case "!=":
            case "<>": return "<>";
            default:   return null;
        }
    }

    private String formatearProducto(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String nombre = rs.getString("nombre");
        String descripcion = rs.getString("descripcion");
        BigDecimal precioVenta = rs.getBigDecimal("precio_venta");
        int stockActual = rs.getInt("stock_actual");
        int stockMinimo = rs.getInt("stock_minimo");

        return String.format(
                "======================== PRODUCTO ========================\r\n" +
                        "id: %d\r\n" +
                        "nombre: %s\r\n" +
                        "descripcion: %s\r\n" +
                        "precio_venta: %s\r\n" +
                        "stock_actual: %d\r\n" +
                        "stock_minimo: %d\r\n" +
                        "==========================================================\r\n",
                id, nombre, descripcion, precioVenta, stockActual, stockMinimo
        );
    }
}
