package Backend.Usuarios.CreateUser;

import Backend.Usuarios.dto.UsuarioDTO;
import Database.PGSQLClient;

import java.sql.*;

public class CreateSQLQuery {
    public String getCreateUserQuery(UsuarioDTO usuarioDTO){
        return String.format(
                """
                INSERT INTO usuario (id, "user" , pass, correo, nombre, telefono, tipo)
                VALUES (%d, '%s', '%s','%s', '%s', %d, '%s')
                """,
                usuarioDTO.id,
                usuarioDTO.username,
                usuarioDTO.password,
                usuarioDTO.correo,
                usuarioDTO.nombre,
                usuarioDTO.telefono,
                usuarioDTO.tipo
        );
    }

    //para respuesta final

    public String executeInsertUserQuery(PGSQLClient pgsqlClient,String query){
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        String tuplas = "";
        int filasAfectadas = 0;
        try{
            Connection connection = DriverManager.getConnection(databaseUrl,pgsqlClient.getUser(),pgsqlClient.getPassword());
            System.out.println("Connecting successfully to database");
            //Consultas
            Statement statement = connection.createStatement();
            filasAfectadas = statement.executeUpdate(query);
            statement.close();
            connection.close();
            return "Insercion Exitosa!\r\n";
        }catch(Exception e){
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage() + "\r\n";
        }
    }
}
