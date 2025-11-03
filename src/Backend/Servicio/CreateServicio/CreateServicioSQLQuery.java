package Backend.Servicio.CreateServicio;

import Backend.Servicio.dto.CreateServicioDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class CreateServicioSQLQuery {

    private static final String INSERT_SERVICIO = """
        INSERT INTO public.servicios (nombre, descripcion, precio, duracion_estimada)
        VALUES (?, ?, ?, ?)
        """;

    public String executeInsertServicioQuery(PGSQLClient pgsqlClient, CreateServicioDTO servicioDTO) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {

            System.out.println("Conectado correctamente a la base de datos");

            try (PreparedStatement ps = connection.prepareStatement(INSERT_SERVICIO)) {
                ps.setString(1, servicioDTO.nombre);
                ps.setString(2, servicioDTO.descripcion);
                ps.setFloat(3, servicioDTO.precio);
                ps.setInt(4, servicioDTO.duracion);

                int filas = ps.executeUpdate();

                if (filas == 0) {
                    return "Error: no se pudo insertar el servicio.";
                }
            }

            return "Servicio creado con éxito:\r\n" +
                    "Nombre: " + servicioDTO.nombre + "\r\n" +
                    "Precio: " + servicioDTO.precio + "\r\n" +
                    "Duración: " + servicioDTO.duracion + " minutos.";

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}
