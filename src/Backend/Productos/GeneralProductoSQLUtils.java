package Backend.Productos;

import Backend.Productos.dto.UpdateProductoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GeneralProductoSQLUtils {
    public static UpdateProductoDTO findProductoById(Connection con, long id) throws SQLException {
        //"INSERT INTO productos (nombre, descripcion, precio_venta, stock_actual, stock_minimo) VALUES (?, ?, ?, ?, ?)";
        String SQL_FIND = "SELECT id, nombre, descripcion, precio_venta, stock_actual, stock_minimo FROM productos WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(SQL_FIND)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UpdateProductoDTO productoDTO = new UpdateProductoDTO();
                    productoDTO.id = rs.getLong("id");
                    productoDTO.nombre = rs.getString("nombre");
                    productoDTO.descripcion = rs.getString("descripcion");
                    productoDTO.precioVenta = rs.getFloat("precio_venta");
                    productoDTO.stockActual = rs.getInt("stock_actual");
                    productoDTO.stockMinimo = rs.getInt("stock_minimo");
                    return productoDTO;
                }
                return null;
            }
        }
    }
}
