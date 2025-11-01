package Backend.Usuarios.ListarUser;
import Database.PGSQLClient;
import java.sql.*;

public class ListarSQLUser {

    private static final String SQL_LISTAR_TODOS =
            "SELECT id, \"user\", pass, correo, nombre, telefono, tipo FROM usuario ORDER BY id ASC";

    private static final String SQL_LISTAR_POR_TIPO =
            "SELECT id, \"user\", pass, correo, nombre, telefono, tipo FROM usuario WHERE tipo = ? ORDER BY id ASC";


    public String executeListarUsuarios(PGSQLClient pgsqlClient, String filtroTipo) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {

            System.out.println("Connecting successfully to database");

            PreparedStatement ps;
            if (filtroTipo.equals("*")) {
                ps = connection.prepareStatement(SQL_LISTAR_TODOS);
            } else {
                ps = connection.prepareStatement(SQL_LISTAR_POR_TIPO);
                ps.setString(1, filtroTipo);
            }

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder result = new StringBuilder();

                while (rs.next()) {
                    long id = rs.getLong("id");
                    String user = rs.getString("user");
                    String pass = rs.getString("pass");
                    String correo = rs.getString("correo");
                    String nombre = rs.getString("nombre");
                    String telefono = rs.getString("telefono");
                    String tipo = rs.getString("tipo");

                    result.append("========================RESULT========================\r\n")
                            .append("id: ").append(id).append("\r\n")
                            .append("user: ").append(user).append("\r\n")
                            .append("pass: ").append(pass).append("\r\n")
                            .append("correo: ").append(correo).append("\r\n")
                            .append("nombre: ").append(nombre).append("\r\n")
                            .append("telefono: ").append(telefono).append("\r\n")
                            .append("tipo: ").append(tipo).append("\r\n")
                            .append("=====================================================================\r\n");
                }

                if (result.length() == 0) {
                    return "No se encontraron usuarios con el filtro: " + filtroTipo + "\r\n";
                }

                result.append(".\r\n");
                return result.toString();
            }

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage() + "\r\n";
        }
    }
}
