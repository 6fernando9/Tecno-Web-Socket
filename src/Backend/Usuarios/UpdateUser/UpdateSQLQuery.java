package Backend.Usuarios.UpdateUser;

import Backend.Usuarios.dto.UpdateUsuarioDTO;
import Backend.Usuarios.dto.UsuarioDTO;
import Database.PGSQLClient;

import java.sql.*;

public class UpdateSQLQuery {
    public String getCreateUserQuery(UpdateUsuarioDTO updateUsuarioDTO){
        return String.format(
                """
                INSERT INTO usuario ("user" , pass, correo, nombre, telefono, tipo)
                VALUES ('%s', '%s','%s', '%s', %d, '%s')
                """,
                updateUsuarioDTO.username,
                updateUsuarioDTO.password,
                updateUsuarioDTO.correo,
                updateUsuarioDTO.nombre,
                updateUsuarioDTO.telefono,
                updateUsuarioDTO.tipo
        );
    }

    //para respuesta final
    private String generarRespuestaPararCrearUsuario(ResultSet resultSet) throws SQLException {
        String result = "";
        while(resultSet.next()) {
            String id = resultSet.getString( "id");
            String user = resultSet.getString( "user");
            String password = resultSet.getString( "pass");
            String correo = resultSet.getString( "correo");
            String nombre = resultSet.getString("nombre");
            String telefono = resultSet.getString( "telefono");
            String tipo = resultSet.getString("tipo");
            result += String.format(
                    "========================RESULT========================\r\n" +
                            "id: %s\r\n" +
                            "user: %s\r\n" +
                            "pass: %s\r\n" +
                            "correo: %s\r\n" +
                            "nombre: %s\r\n" +
                            "telefono: %s\r\n" +
                            "tipo: %s\r\n" +
                            "=====================================================================\r\n"
                    ,id,user,password,correo,nombre,telefono,tipo
            );
        }
        result += ".\r\n";
        resultSet.close();
        return result;
    }
    public String executeUpdateUserQuery(PGSQLClient pgsqlClient, String query){
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
            return "Actualizacion Exitosa!\r\n";
        }catch(Exception e){
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage() + "\r\n";
        }
    }
}
