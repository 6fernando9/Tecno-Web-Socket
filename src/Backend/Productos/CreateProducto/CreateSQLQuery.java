package Backend.Productos.CreateProducto;

import Backend.Productos.dto.CreateProductoDTO;
import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.CreateUsuarioDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class CreateSQLQuery {
    private static final String SQL_INSERT =
            "INSERT INTO productos (nombre, descripcion, precio_venta, stock_actual, stock_minimo) VALUES (?, ?, ?, ?, ?)";
    public String executeInsertProductoQuery(PGSQLClient pgsqlClient, CreateProductoDTO createProductoDTO) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");

            try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
                ps.setString(1, createProductoDTO.nombre);
                ps.setString(2, createProductoDTO.descripcion);
                ps.setFloat(3, createProductoDTO.precioVenta);
                ps.setInt(4, createProductoDTO.stockActual);
                ps.setInt(5, createProductoDTO.stockMinimo);
                int filas = ps.executeUpdate();
                if (filas == 0) {
                    return "Error: no se pudo insertar el usuario";
                }
                return String.format(
                        "Producto creado exitosamente:\r\n" +
                                "-----------------------------\r\n" +
                                "Nombre: %s\r\n" +
                                "Descripción: %s\r\n" +
                                "Precio Venta: %.2f\r\n" +
                                "Stock Actual: %d\r\n" +
                                "Stock Mínimo: %d\r\n" +
                                "-----------------------------\r\n",
                        createProductoDTO.nombre,
                        createProductoDTO.descripcion,
                        createProductoDTO.precioVenta,
                        createProductoDTO.stockActual,
                        createProductoDTO.stockMinimo
                );
            }

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}
