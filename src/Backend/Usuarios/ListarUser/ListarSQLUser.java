package Backend.Usuarios.ListarUser;
import Database.PGSQLClient;
import java.sql.*;

public class ListarSQLUser {

    private static final String SQL_LISTAR_TODOS =
            "SELECT id, nombre, apellido, email, telefono, password, rol FROM usuarios ORDER BY id ASC";

    private static final String SQL_LISTAR_POR_ROL =
            "SELECT id, nombre, apellido, email, telefono, password, rol FROM usuarios WHERE rol = ? ORDER BY id ASC";


    public String executeListarUsuarios(PGSQLClient pgsqlClient, String filtroRol) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {

            System.out.println("Connecting successfully to database");

            PreparedStatement ps;
            if (filtroRol.equals("*")) {
                ps = connection.prepareStatement(SQL_LISTAR_TODOS);
            } else {
                ps = connection.prepareStatement(SQL_LISTAR_POR_ROL);
                ps.setString(1, filtroRol);
            }

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder result = new StringBuilder();

                while (rs.next()) {
                    result.append(formatearUsuario(rs));
                }

                if (result.length() == 0) {
                    return "[]\r\n";
                }

                result.append(".\r\n");
                return result.toString();
            }

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage() + "\r\n";
        }
    }

    private String formatearUsuario(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        String email = rs.getString("email");
        String telefono = rs.getString("telefono");
        String password = rs.getString("password");
        String rol = rs.getString("rol");

        return String.format(
                "======================== USUARIO ========================\r\n" +
                        "id: %d\r\n" +
                        "nombre: %s\r\n" +
                        "apellido: %s\r\n" +
                        "email: %s\r\n" +
                        "telefono: %s\r\n" +
                        "password: %s\r\n" +
                        "rol: %s\r\n" +
                        "==========================================================\r\n",
                id, nombre, apellido, email, telefono, password, rol
        );
    }
}
