package Backend.Usuarios.UpdateUser;

import Backend.Roles;
import Backend.Usuarios.CreateUser.CreateSQLQuery;
import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.UpdateUsuarioDTO;
import Database.PGSQLClient;

import java.sql.*;

public class UpdateSQLQuery {
    private static final String SQL_UPDATE =
            "UPDATE users SET name = ?, apellido = ?, email = ?, telefono = ?, password = ?, rol = ? WHERE id = ?";

    public String executeUpdateUserQuery(PGSQLClient pgsqlClient, UpdateUsuarioDTO dto){
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try{
            Connection connection = DriverManager.getConnection(databaseUrl,pgsqlClient.getUser(),pgsqlClient.getPassword());
            System.out.println("Connecting successfully to database");
            UpdateUsuarioDTO usuarioDTODB = GeneralUsuarioSQLUtils.findUserById(connection, dto.id);
            //usuario no esta en la base de datos
            if (usuarioDTODB == null) {
                return "No existe un usuario con id=" + dto.id + ". No se realizó ninguna actualización.";
            }

            if(!usuarioDTODB.email.equals(dto.email)){
                //entonces busca si existe algun email ya registrado en la bd
                if(GeneralUsuarioSQLUtils.existeUsuarioPorEmail(connection, dto.email)){
                    return "El usuario ya se encuentra registrado en el Sistema";
                }
                //si no existe entonces realiza el update
            }
            if(dto.rol.equalsIgnoreCase(Roles.CLIENTE.getDescripcion())){
                boolean existeCliente = GeneralUsuarioSQLUtils.existeUsuarioConRol(connection, usuarioDTODB.id, Roles.CLIENTE.getDescripcion());
                System.out.println("existe cliente " + existeCliente);
                if(!existeCliente){
                    CreateSQLQuery.insertIntoRolTable(connection, dto.id,Roles.CLIENTE.getDescripcion());
                }
            }else if(dto.rol.equalsIgnoreCase(Roles.BARBERO.getDescripcion())){
                boolean existeBarbero = GeneralUsuarioSQLUtils.existeUsuarioConRol(connection, usuarioDTODB.id, Roles.BARBERO.getDescripcion());
                System.out.println("existe barbero " + existeBarbero);
                if(!existeBarbero){
                    CreateSQLQuery.insertIntoRolTable(connection, dto.id,Roles.BARBERO.getDescripcion());
                }
            }

            //si los emails son iguales igual que actualize
            try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
                ps.setString(1, dto.nombre);
                ps.setString(2, dto.apellido);
                ps.setString(3, dto.email);
                ps.setString(4, dto.telefono);
                ps.setString(5, dto.password);
                ps.setString(6, dto.rol);
                ps.setLong(7, dto.id);
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
                        dto.id,
                        dto.nombre,
                        dto.apellido,
                        dto.email,
                        dto.telefono,
                        dto.rol
                );
            }
        }catch(Exception e){
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage() ;
        }
    }
}
