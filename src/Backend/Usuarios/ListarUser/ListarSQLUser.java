package Backend.Usuarios.ListarUser;
import Database.PGSQLClient;
import java.sql.*;

public class ListarSQLUser {

    private static final String SQL_LISTAR_TODOS =
            "SELECT id, nombre, apellido, email, telefono, rol FROM usuarios ORDER BY id ASC";

    private static final String SQL_LISTAR_POR_ROL =
            "SELECT id, nombre, apellido, email, telefono, rol FROM usuarios WHERE rol = ? AND deleted_at is null ORDER BY id ASC";


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
                //StringBuilder result = new StringBuilder();
                StringBuilder result = new StringBuilder();

                int contador = 1;

                while (rs.next()) {
                    result.append(formatearUsuario(rs, contador++));
                }

                if (result.length() == 0) {
                    return "\n⚠️ No se encontraron usuarios registrados.\n";
                }
                ///result.append(".");
                return result.toString();
            }

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }



    private String formatearUsuario(ResultSet rs, int numero) throws SQLException {
        long id = rs.getLong("id");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        String email = rs.getString("email");
        String telefono = rs.getString("telefono");
        String rol = rs.getString("rol");

        return String.format(
                "----------------------------------------------------\r\n" +
                "Usuario %d:\r\n" +
                        "ID: %d\r\n" +
                        "Nombre: %s\r\n" +
                        "Apellido: %s\r\n" +
                        "Email: %s\r\n" +
                        "Teléfono: %s\r\n" +
                        "Rol: %s\r\n" +
                        "----------------------------------------------------\r\n",
                numero, id, nombre, apellido, email, telefono, rol
        );
    }
}
