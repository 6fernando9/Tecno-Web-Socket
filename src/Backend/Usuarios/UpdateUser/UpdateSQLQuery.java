package Backend.Usuarios.UpdateUser;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.UpdateUsuarioDTO;
import Database.PGSQLClient;

import java.sql.*;

public class UpdateSQLQuery {
    private static final String SQL_UPDATE =
            "UPDATE usuarios SET nombre = ?, apellido = ?, email = ?, telefono = ?, password = ?, rol = ? WHERE id = ?";

    public String executeUpdateUserQuery(PGSQLClient pgsqlClient, UpdateUsuarioDTO updateUsuarioDTO){
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try{
            Connection connection = DriverManager.getConnection(databaseUrl,pgsqlClient.getUser(),pgsqlClient.getPassword());
            System.out.println("Connecting successfully to database");
            UpdateUsuarioDTO usuarioDTODB = GeneralUsuarioSQLUtils.findUserById(connection,updateUsuarioDTO.id);
            //usuario no esta en la base de datos
            if (usuarioDTODB == null) {
                return "No existe un usuario con id=" + updateUsuarioDTO.id + ". No se realizó ninguna actualización.";
            }
            //si el usuario si esta presente
            //emails diferentes
            if(!usuarioDTODB.email.equals(updateUsuarioDTO.email)){
                //entonces busca si existe algun email ya registrado en la bd
                if(GeneralUsuarioSQLUtils.existeUsuarioPorEmail(connection,updateUsuarioDTO.email)){
                    return "El usuario ya se encuentra registrado en el Sistema";
                }
                //si no existe entonces realiza el update
            }
            //si los emails son iguales igual que actualize
            try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
                ps.setString(1, updateUsuarioDTO.nombre);
                ps.setString(2, updateUsuarioDTO.apellido);
                ps.setString(3, updateUsuarioDTO.email);
                ps.setString(4, updateUsuarioDTO.telefono);
                ps.setString(5, updateUsuarioDTO.password);
                ps.setString(6, updateUsuarioDTO.rol);
                ps.setLong(7, updateUsuarioDTO.id);
                int filas = ps.executeUpdate();
                if (filas == 0) {
                    return "El usuario fue modificado/eliminado durante la operación. No se actualizó nada.";
                }
                return String.format(
                        "Usuario actualizado correctamente:\r\n" +
                                "--------------------------\r\n" +
                                "ID: %d\r\n" +
                                "Nombre: %s\r\n" +
                                "Apellido: %s\r\n" +
                                "Email: %s\r\n" +
                                "Teléfono: %s\r\n" +
                                "Rol: %s\r\n" +
                                "--------------------------\r\n",
                        updateUsuarioDTO.id,
                        updateUsuarioDTO.nombre,
                        updateUsuarioDTO.apellido,
                        updateUsuarioDTO.email,
                        updateUsuarioDTO.telefono,
                        updateUsuarioDTO.rol
                );
            }
        }catch(Exception e){
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage() ;
        }
    }
}
