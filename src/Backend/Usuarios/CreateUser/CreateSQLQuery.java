package Backend.Usuarios.CreateUser;

import Backend.Usuarios.dto.UsuarioDTO;
import Database.PGSQLClient;

import java.sql.*;

public class CreateSQLQuery {
    public String getCreateUserQuery(UsuarioDTO usuarioDTO){
        return String.format(
                """
                INSERT INTO usuario (id, nombre, password, correo, telefono, tipo)
                VALUES (%d, '%s', '%s', '%s', %d, '%s')
                """,
                usuarioDTO.id,
                usuarioDTO.nombre,
                usuarioDTO.password,
                usuarioDTO.correo,
                usuarioDTO.telefono,
                usuarioDTO.tipo
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
                            "password: %s\r\n" +
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
    public String executePGSQLClientForPersonTableForPatronQuery(PGSQLClient pgsqlClient,String query){
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        String tuplas = "";
        try{
            Connection connection = DriverManager.getConnection(databaseUrl,pgsqlClient.getUser(),pgsqlClient.getPassword());
            System.out.println("Connecting successfully to database");
            //Consultas
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            //printResultForQueryTablePerson(resultSet);
            tuplas = this.generarRespuestaPararCrearUsuario(resultSet);
        }catch(Exception e){
            System.out.println("Throw: " + e.getMessage());
        }
        return tuplas;
    }
}
