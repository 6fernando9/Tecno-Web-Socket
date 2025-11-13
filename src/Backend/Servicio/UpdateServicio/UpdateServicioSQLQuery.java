package Backend.Servicio.UpdateServicio;

import Backend.Servicio.GeneralServicioSQLUtils;
import Backend.Servicio.dto.UpdateServicioDTO;
import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UpdateServicioSQLQuery {
    private static final String SQL_UPDATE = """
        UPDATE public.servicios
           SET nombre = ?, descripcion = ?, precio = ?, duracion_estimada = ?
         WHERE id = ?
        """;

    private static final String SQL_FIND_BY_ID = """
        SELECT id, nombre, descripcion, precio, duracion_estimada
          FROM public.servicios
         WHERE id = ?
        """;

    public String executeUpdateServicio(PGSQLClient pgsqlClient, UpdateServicioDTO dto){
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try{
            Connection connection = DriverManager.getConnection(databaseUrl,pgsqlClient.getUser(),pgsqlClient.getPassword());
            System.out.println("Connecting successfully to database");
            UpdateServicioDTO updateServicioDTODB = GeneralServicioSQLUtils.findServicioById(connection,dto.id);
            //servicio no esta en la base de datos
            if (updateServicioDTODB == null) {
                return "No existe un servicio con id=" + dto.id + ". No se realizó ninguna actualización.";
            }
            //todo OK
            try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
                ps.setString(1, dto.nombre);
                ps.setString(2, dto.descripcion);
                ps.setFloat(3, dto.precio);
                ps.setInt(4, dto.duracion);
                ps.setLong(5, dto.id);
                int filas = ps.executeUpdate();
                if (filas == 0) {
                    return "El servicio fue modificado/eliminado durante la operación. No se actualizó nada.";
                }
                return String.format(
                        "Servicio actualizado exitosamente:\r\n" +
                                "----------------------------------\r\n" +
                                "ID: %d\r\n" +
                                "Nombre: %s\r\n" +
                                "Descripción: %s\r\n" +
                                "Precio: %.2f\r\n" +
                                "Duración Estimada: %d minutos\r\n" +
                                "----------------------------------\r\n",
                        dto.id,
                        dto.nombre,
                        dto.descripcion,
                        dto.precio,
                        dto.duracion
                );
            }
        }catch(Exception e){
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage() ;
        }
    }
}
