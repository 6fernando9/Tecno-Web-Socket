package Backend.Servicio.CambiarEstado;

import Backend.Servicio.GeneralServicioSQLUtils;
import Backend.Servicio.dto.ServicioEstadoDTO;
import Backend.Servicio.dto.UpdateServicioDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CambiarEstadoServicioSQL {
    private static final String SQL_UPDATE =
            "UPDATE servicios SET estado = ?, deleted_at = ? WHERE id = ?";

    public String executeUpdateEstadoServicio(PGSQLClient pgsqlClient, ServicioEstadoDTO dto){
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try{
            Connection connection = DriverManager.getConnection(databaseUrl,pgsqlClient.getUser(),pgsqlClient.getPassword());
            System.out.println("Connecting successfully to database");
            UpdateServicioDTO servicioDB = GeneralServicioSQLUtils.findServicioById(connection,dto.id);
            //usuario no esta en la base de datos
            if (servicioDB == null) {
                return "No existe un servicio con id=" + dto.id + ". No se realizó ninguna actualización.";
            }

            Timestamp deleteAt = null;

            if (dto.estado.equalsIgnoreCase("eliminado")) {
                if (servicioDB.estado.equalsIgnoreCase("activo")) {
                    deleteAt = Timestamp.valueOf(LocalDateTime.now());
                } else if (servicioDB.estado.equalsIgnoreCase("eliminado")) {
                    deleteAt = servicioDB.deletedAt != null
                            ? Timestamp.valueOf(servicioDB.deletedAt)
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
                    return "El Servicio fue modificado/eliminado durante la operación. No se actualizó nada.";
                }

                // ✅ Salida formateada con todos los campos principales
                return String.format(
                        "✅ Estado del servicio actualizado correctamente:\r\n" +
                                "---------------------------------\r\n" +
                                "ID: %d\r\n" +
                                "Nombre: %s\r\n" +
                                "Descripción: %s\r\n" +
                                "Duración estimada: %d min\r\n" +
                                "Precio: %s Bs\r\n" +
                                "Estado: %s\r\n" +
                                "---------------------------------\r\n",
                        servicioDB.id,
                        servicioDB.nombre,
                        servicioDB.descripcion,
                        servicioDB.duracion,
                        servicioDB.precio,
                        dto.estado
                );

            }
        }catch(Exception e){
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage() ;
        }
    }
}
