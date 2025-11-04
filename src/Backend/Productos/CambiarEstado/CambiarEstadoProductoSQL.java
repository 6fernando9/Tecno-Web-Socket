package Backend.Productos.CambiarEstado;

import Backend.Productos.GeneralProductoSQLUtils;
import Backend.Productos.dto.ProductoEstadoDTO;
import Backend.Productos.dto.UpdateProductoDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CambiarEstadoProductoSQL {
    private static final String SQL_UPDATE =
            "UPDATE productos SET estado = ?, deleted_at = ? WHERE id = ?";

    public String executeUpdateEstadoProducto(PGSQLClient pgsqlClient, ProductoEstadoDTO dto){
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try{
            Connection connection = DriverManager.getConnection(databaseUrl,pgsqlClient.getUser(),pgsqlClient.getPassword());
            System.out.println("Connecting successfully to database");
            UpdateProductoDTO productoDB = GeneralProductoSQLUtils.findProductoById(connection,dto.id);
            //usuario no esta en la base de datos
            if (productoDB == null) {
                return "No existe un producto con id=" + dto.id + ". No se realizó ninguna actualización.";
            }

            Timestamp deleteAt = null;

            if (dto.estado.equalsIgnoreCase("eliminado")) {
                if (productoDB.estado.equalsIgnoreCase("activo")) {
                    deleteAt = Timestamp.valueOf(LocalDateTime.now());
                } else if (productoDB.estado.equalsIgnoreCase("eliminado")) {
                    deleteAt = productoDB.deleteAt != null
                            ? Timestamp.valueOf(productoDB.deleteAt)
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
                    return "El producto fue modificado/eliminado durante la operación. No se actualizó nada.";
                }

                // ✅ Salida formateada con todos los campos principales
                return String.format(
                        "✅ Estado del producto actualizado correctamente:\r\n" +
                                "-------------------------------\r\n" +
                                "ID: %d\r\n" +
                                "Nombre: %s\r\n" +
                                "Descripción: %s\r\n" +
                                "Precio venta: %s\r\n" +
                                "Stock actual: %d\r\n" +
                                "Stock mínimo: %d\r\n" +
                                "Estado: %s\r\n" +
                                "-------------------------------\r\n",
                        productoDB.id,
                        productoDB.nombre,
                        productoDB.descripcion,
                        productoDB.precioVenta,
                        productoDB.stockActual,
                        productoDB.stockMinimo,
                        dto.estado
                );

            }
        }catch(Exception e){
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage() ;
        }
    }
}
