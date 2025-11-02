package Backend.Usuarios.DeleteUser;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DeleteSQLQuery {
    private static final String SQL_DELETE = "DELETE FROM usuarios WHERE id = ?";
    public String executeDeleteUserQuery(PGSQLClient pgsqlClient, long id) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try {
            Connection connection = DriverManager.getConnection(databaseUrl,pgsqlClient.getUser(),pgsqlClient.getPassword());
            System.out.println("Connecting successfully to database");
            if (!GeneralUsuarioSQLUtils.existsUser(connection, id)) {
                return "No existe un usuario con id=" + id + ". No se realizó ninguna eliminación.\r\n";
            }
            try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
                ps.setLong(1, id);
                int filas = ps.executeUpdate();

                if (filas == 0) {
                    return "El usuario fue eliminado/modificado durante la operación. No se eliminó ninguna fila.\r\n";
                }

                return "Eliminación exitosa (" + filas + " fila(s)).\r\n";
            }

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage() + "\r\n";
        }
    }
}
