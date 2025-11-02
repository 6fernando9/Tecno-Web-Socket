package Backend.Productos.UpdateProducto;

import Backend.Productos.GeneralProductoSQLUtils;
import Backend.Productos.dto.UpdateProductoDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UpdateSQLQuery {
    private static final String SQL_UPDATE =
            "UPDATE productos SET nombre = ?, descripcion = ?, precio_venta = ?, stock_actual = ?, stock_minimo = ? WHERE id = ?";

    public String executeUpdateProductoQuery(PGSQLClient pgsqlClient, UpdateProductoDTO updateProductoDTO) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try {
            Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword());
            System.out.println("Connecting successfully to database");
            UpdateProductoDTO updateProductoDTDB = GeneralProductoSQLUtils.findProductoById(connection, updateProductoDTO.id);
            if (updateProductoDTDB == null) {
                return "Error El producto no se encuentra en el sistema";
            }
            //si los emails son iguales igual que actualize
            try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
                ps.setString(1, updateProductoDTO.nombre);
                ps.setString(2, updateProductoDTO.descripcion);
                ps.setFloat(3, updateProductoDTO.precioVenta);
                ps.setInt(4, updateProductoDTO.stockActual);
                ps.setInt(5, updateProductoDTO.stockMinimo);

                int filas = ps.executeUpdate();
                if (filas == 0) {
                    return "El Producto fue modificado/eliminado durante la operación. No se actualizó nada.";
                }
                return "Actualización exitosa (" + filas + " fila(s)).";
            }
        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}

