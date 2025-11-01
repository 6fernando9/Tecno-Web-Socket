package Backend.Usuarios.UpdateUser;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.UpdateUsuarioDTO;
import Database.PGSQLClient;

import java.sql.*;

public class UpdateSQLQuery {
    private static final String SQL_UPDATE =
            "UPDATE usuario SET \"user\" = ?, pass = ?, correo = ?, nombre = ?, telefono = ?, tipo = ? WHERE id = ?";

//    public String getUpdateUserQuery(UpdateUsuarioDTO updateUsuarioDTO){
//        return String.format(
//                """
//                UPDATE usuario
//                SET "user" = '%s',
//                    pass = '%s',
//                    correo = '%s',
//                    nombre = '%s',
//                    telefono = %d,
//                    tipo = '%s'
//                WHERE id = %d
//                """,
//                updateUsuarioDTO.username,
//                updateUsuarioDTO.password,
//                updateUsuarioDTO.correo,
//                updateUsuarioDTO.nombre,
//                updateUsuarioDTO.telefono,
//                updateUsuarioDTO.tipo,
//                updateUsuarioDTO.id
//        );
//    }


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
//    public String executeUpdateUserQuery(PGSQLClient pgsqlClient, UpdateUsuarioDTO updateUsuarioDTO){
//        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
//        try{
//            Connection connection = DriverManager.getConnection(databaseUrl,pgsqlClient.getUser(),pgsqlClient.getPassword());
//            System.out.println("Connecting successfully to database");
//            //Consultas
//            if (!GeneralUsuarioSQLUtils.existsUser(connection, updateUsuarioDTO.id)) {
//                return "No existe un usuario con id=" + updateUsuarioDTO.id + ". No se realizó ninguna actualización.\r\n";
//            }
//            try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
//                ps.setString(1, updateUsuarioDTO.username);
//                ps.setString(2, updateUsuarioDTO.password);
//                ps.setString(3, updateUsuarioDTO.correo);
//                ps.setString(4, updateUsuarioDTO.nombre);
//                ps.setLong(5, updateUsuarioDTO.telefono);
//                ps.setString(6, updateUsuarioDTO.tipo);
//                ps.setLong(7, updateUsuarioDTO.id);
//                int filas = ps.executeUpdate();
//                if (filas == 0) {
//                    return "El usuario fue modificado/eliminado durante la operación. No se actualizó nada.\r\n";
//                }
//                return "Actualización exitosa (" + filas + " fila(s)).\r\n";
//            }
//        }catch(Exception e){
//            System.out.println("Throw: " + e.getMessage());
//            return "ERROR DE BASE DE DATOS: " + e.getMessage() + "\r\n";
//        }
//    }
}
