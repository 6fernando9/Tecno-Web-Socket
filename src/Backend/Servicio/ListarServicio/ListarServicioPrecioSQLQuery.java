package Backend.Servicio.ListarServicio;

import Backend.Utils.dto.ComparadorSigno;
import Database.PGSQLClient;

import java.math.BigDecimal;
import java.sql.*;

public class ListarServicioPrecioSQLQuery {

    private static final String LIST_BASE_SELECT =
            "SELECT id, nombre, descripcion, duracion_estimada, precio FROM servicios";

    public String executeListarServicios(PGSQLClient pgsqlClient, ComparadorSigno comparador) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {

            System.out.println("Connecting successfully to database");

            String sql;
            PreparedStatement ps;

            if (comparador == null) {
                // Sin filtro: listar todos
                sql = LIST_BASE_SELECT + " ORDER BY id ASC";
                ps = connection.prepareStatement(sql);
            } else {
                // Validar operador
                String operador = mapOperadorSeguro(comparador.signo);
                if (operador == null) {
                    return "Error: operador inválido: " + comparador.signo;
                }

                sql = LIST_BASE_SELECT + " WHERE precio " + operador + " ? ORDER BY id ASC";
                ps = connection.prepareStatement(sql);
                ps.setBigDecimal(1, new BigDecimal(comparador.valor.toString()));
            }

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder result = new StringBuilder();

                while (rs.next()) {
                    result.append(formatearServicio(rs));
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

    public String executeListarServiciosBetween(PGSQLClient pgsqlClient, int[] intervalo) {
        if (intervalo == null || intervalo.length != 2) {
            return "Error: se esperaban dos valores (mínimo y máximo)";
        }

        int min = intervalo[0];
        int max = intervalo[1];

        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        String sql = LIST_BASE_SELECT + " WHERE precio BETWEEN ? AND ? ORDER BY id ASC";

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword());
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBigDecimal(1, new BigDecimal(min));
            ps.setBigDecimal(2, new BigDecimal(max));

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder result = new StringBuilder();

                while (rs.next()) {
                    result.append(formatearServicio(rs));
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

    // ====== MAPEO DE OPERADORES ======
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

    // ====== FORMATEO DE RESULTADOS ======
    private String formatearServicio(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String nombre = rs.getString("nombre");
        String descripcion = rs.getString("descripcion");
        int duracion = rs.getInt("duracion_estimada");
        BigDecimal precio = rs.getBigDecimal("precio");

        return String.format(
                "======================== SERVICIO ========================\r\n" +
                        "id: %d\r\n" +
                        "nombre: %s\r\n" +
                        "descripcion: %s\r\n" +
                        "duracion_estimada: %d minutos\r\n" +
                        "precio: %s Bs\r\n" +
                        "==========================================================\r\n",
                id, nombre, descripcion, duracion, precio
        );
    }
}
