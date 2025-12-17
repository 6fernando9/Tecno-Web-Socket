package Backend.Usuarios.CreateUser;

import Backend.Roles;
import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.CreateUsuarioDTO;
import Database.PGSQLClient;

import java.sql.*;

public class CreateSQLQuery {

    private static final String SQL_INSERT_USER =
            "INSERT INTO users (name, apellido, email, telefono, password, rol) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
    private static final String SQL_INSERT_BARBERO =
            "INSERT INTO barberos (id) VALUES (?)";
    private static final String SQL_INSERT_CLIENTE =
            "INSERT INTO clientes (id) VALUES (?)";

    public String executeInsertUserQuery(PGSQLClient pgsqlClient, CreateUsuarioDTO dto) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");
            if (GeneralUsuarioSQLUtils.existeUsuarioPorEmail(connection, dto.email)) {
                return "Error: ya existe un usuario con el correo '" + dto.email + "'.";
            }
            long nuevoUserId = insertIntoUsers(connection, dto);

            insertIntoRolTable(connection, nuevoUserId, dto.rol);

            return String.format(
                    "Usuario creado exitosamente:\r\n" +
                            "--------------------------\r\n" +
                            "Id: %d\r\n" +
                            "Nombre: %s\r\n" +
                            "Apellido: %s\r\n" +
                            "Email: %s\r\n" +
                            "Teléfono: %s\r\n" +
                            "Rol: %s\r\n" +
                            "--------------------------\r\n",
                    nuevoUserId,
                    dto.nombre,
                    dto.apellido,
                    dto.email,
                    dto.telefono,
                    dto.rol
            );
        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
    private long insertIntoUsers(Connection connection, CreateUsuarioDTO dto) throws SQLException {
        long userId = -1;
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dto.nombre);
            ps.setString(2, dto.apellido);
            ps.setString(3, dto.email);
            ps.setString(4, dto.telefono);
            ps.setString(5, dto.password);
            ps.setString(6, dto.rol);

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new SQLException("No se pudo insertar el usuario, no se obtuvieron filas afectadas.");
            }

            // Obtener el ID generado (PostgreSQL lo retorna con RETURNING id)
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    userId = rs.getLong(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID del usuario insertado.");
                }
            }
        }
        return userId;
    }

    public static void insertIntoRolTable(Connection connection, long userId, String rol) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

            if(rol.equalsIgnoreCase(Roles.CLIENTE.getDescripcion())){
               sql = SQL_INSERT_CLIENTE;
            } else if (rol.equalsIgnoreCase(Roles.BARBERO.getDescripcion())) {
               sql = SQL_INSERT_BARBERO;
            }

            if (sql != null) {
                ps = connection.prepareStatement(sql);
                ps.setLong(1, userId);
                int filas = ps.executeUpdate();
                if (filas == 0) {
                    throw new SQLException("Fallo al insertar el rol '" + rol + "' en su tabla específica.");
                }
            }

    }

}
