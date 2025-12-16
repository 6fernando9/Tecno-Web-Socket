package Backend.Usuarios.CambiarEstado;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.UpdateUsuarioDTO;
import Backend.Usuarios.dto.UsuarioEstadoDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CambiarEstadoUsuarioSQL {
    private static final String SQL_UPDATE =
            "UPDATE users SET estado = ?, deleted_at = ? WHERE id = ?";

    public String executeUpdateEstadoUsuario(PGSQLClient pgsqlClient, UsuarioEstadoDTO dto){
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try{
            Connection connection = DriverManager.getConnection(databaseUrl,pgsqlClient.getUser(),pgsqlClient.getPassword());
            System.out.println("Connecting successfully to database");
            UpdateUsuarioDTO usuarioDTODB = GeneralUsuarioSQLUtils.findUserById(connection,dto.id);
            //usuario no esta en la base de datos
            if (usuarioDTODB == null) {
                return "No existe un usuario con id=" + dto.id + ". No se realizó ninguna actualización.";
            }

            Timestamp deleteAt = null;

            if (dto.estado.equalsIgnoreCase("eliminado")) {
                if (usuarioDTODB.estado.equalsIgnoreCase("activo")) {
                    deleteAt = Timestamp.valueOf(LocalDateTime.now());
                } else if (usuarioDTODB.estado.equalsIgnoreCase("eliminado")) {
                    deleteAt = usuarioDTODB.deletedAt != null
                            ? Timestamp.valueOf(usuarioDTODB.deletedAt)
                            : Timestamp.valueOf(LocalDateTime.now());
                }
            }

            // si el usuario pasa a activo, eliminamos la fecha
            if (dto.estado.equalsIgnoreCase("activo")) {
                deleteAt = null;
            }
            //en el caso de que el dto sea estado eliminado y el usuario db sea eliminado
            //si los emails son iguales igual que actualize
            try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
                ps.setString(1, dto.estado);
                ps.setTimestamp(2, deleteAt);
                ps.setLong(3, dto.id);
                int filas = ps.executeUpdate();
                if (filas == 0) {
                    return "El usuario fue modificado/eliminado durante la operación. No se actualizó nada.";
                }

                // ✅ Salida formateada con todos los campos principales
                return String.format(
                        "Estado del usuario actualizado correctamente:\r\n" +
                                "--------------------------\r\n" +
                                "ID: %d\r\n" +
                                "Nombre: %s\r\n" +
                                "Apellido: %s\r\n" +
                                "Email: %s\r\n" +
                                "Teléfono: %s\r\n" +
                                "Rol: %s\r\n" +
                                "Estado: %s\r\n" +
                                "--------------------------\r\n",
                        usuarioDTODB.id,
                        usuarioDTODB.nombre,
                        usuarioDTODB.apellido,
                        usuarioDTODB.email,
                        usuarioDTODB.telefono,
                        usuarioDTODB.rol,
                        dto.estado
                );
            }
        }catch(Exception e){
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage() ;
        }
    }
}
