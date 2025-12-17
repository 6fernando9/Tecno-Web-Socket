package Backend.Movimientos;

import Backend.Movimientos.dto.MovimientoDTO;
import Backend.Productos.dto.UpdateProductoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class GeneralMovimientoUtils {
    public static String[] TIPO_MOVIMIENTO = {"entrada","salida","ajuste"};
    public static boolean esTipoMovimientoPermitido(String tipoMovimiento) {
        return Arrays.asList(TIPO_MOVIMIENTO).contains(tipoMovimiento.toLowerCase());
    }

    public static MovimientoDTO findMovimientoConProductoById(Connection con, long movimientoId) throws SQLException {
        String sql = """
            SELECT m.id AS m_id, m.tipo_movimiento, m.cantidad, m.motivo, m.fecha, m.estado as m_estado,
                   p.id AS p_id, p.nombre, p.descripcion, p.precio_venta, 
                   p.stock_minimo, p.stock_actual, p.estado, p.deleted_at
            FROM movimiento_inventarios m
            JOIN productos p ON m.producto_id = p.id
            WHERE m.id = ?;
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, movimientoId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    UpdateProductoDTO productoDTO = new UpdateProductoDTO(
                            rs.getLong("p_id"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getFloat("precio_venta"),
                            rs.getInt("stock_minimo"),
                            rs.getInt("stock_actual"),
                            rs.getString("estado"),
                            rs.getString("deleted_at")
                    );

                    return new MovimientoDTO(
                            rs.getLong("m_id"),
                            rs.getString("tipo_movimiento"),
                            rs.getInt("cantidad"),
                            rs.getString("motivo"),
                            rs.getString("fecha"),
                            rs.getString("m_estado"),
                            productoDTO
                    );
                }
            }
        }
        return null;
    }
}
