package Backend.Usuarios.CreateUser;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.CreateUsuarioDTO;
import Database.PGSQLClient;

import java.sql.*;

public class CreateSQLQuery {
    private static final String SQL_INSERT =
            "INSERT INTO usuarios (nombre, apellido, email, telefono, password, rol) VALUES (?, ?, ?, ?, ?, ?)";
    public String executeInsertUserQuery(PGSQLClient pgsqlClient, CreateUsuarioDTO createUsuarioDTO) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");
            if (GeneralUsuarioSQLUtils.existeUsuarioPorEmail(connection, createUsuarioDTO.email)) {
                return "Error: ya existe un usuario con el correo '" + createUsuarioDTO.email + "'.";
            }

            try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
                ps.setString(1, createUsuarioDTO.nombre);
                ps.setString(2, createUsuarioDTO.apellido);
                ps.setString(3, createUsuarioDTO.email);
                ps.setString(4, createUsuarioDTO.telefono);
                ps.setString(5, createUsuarioDTO.password);
                ps.setString(6, createUsuarioDTO.rol);

                int filas = ps.executeUpdate();

                if (filas == 0) {
                    return "Error: no se pudo insertar el usuario.";
                }
            }
            return "Usuario insertado con exito!";
        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

}
