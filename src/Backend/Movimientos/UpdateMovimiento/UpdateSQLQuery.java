package Backend.Movimientos.UpdateMovimiento;

import Backend.Movimientos.dto.UpdateMovimientoDTO;
import Backend.Productos.GeneralProductoSQLUtils;
import Backend.Productos.dto.UpdateProductoDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class UpdateSQLQuery {

    public String executeUpdateMovimiento(PGSQLClient pgsqlClient, UpdateMovimientoDTO dto) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");
            connection.setAutoCommit(false);

            // Obtener movimiento actual
            String SQL_FIND = "SELECT id, producto_id, usuario_id, tipo_movimiento, cantidad, fecha, motivo FROM movimiento_inventarios WHERE id = ? FOR UPDATE";
            long productoId;
            String tipo;
            int cantidadActual;
            try (PreparedStatement psFind = connection.prepareStatement(SQL_FIND)) {
                psFind.setLong(1, dto.id);
                try (ResultSet rs = psFind.executeQuery()) {
                    if (!rs.next()) {
                        return "Error: movimiento no encontrado (id=" + dto.id + ")";
                    }
                    productoId = rs.getLong("producto_id");
                    tipo = rs.getString("tipo_movimiento");
                    cantidadActual = rs.getInt("cantidad");
                }
            }

            // Si no hay campos para actualizar
            if (dto.cantidad == null && (dto.motivo == null || dto.motivo.isBlank()) && (dto.fecha == null || dto.fecha.isBlank())) {
                return "Error: no hay campos para actualizar";
            }

            // No permitimos cambiar tipo o producto_id a través de este endpoint
            // Si se requiere, se debe revertir y crear uno nuevo (audit trail)

            // Si se solicita cambiar cantidad pero no es ajuste => error
            if (dto.cantidad != null && !"ajuste".equalsIgnoreCase(tipo)) {
                return "Error: solo se permite cambiar la cantidad en movimientos de tipo 'ajuste'";
            }

            int nuevoStock = -1;

            if (dto.cantidad != null) {
                // obtener producto
                UpdateProductoDTO producto = GeneralProductoSQLUtils.findProductoById(connection, productoId);
                if (producto == null) {
                    connection.rollback();
                    return "Error: producto asociado no encontrado (id=" + productoId + ")";
                }
                int delta = dto.cantidad - cantidadActual;
                nuevoStock = producto.stockActual + delta;
            }

            // Actualizar movimiento
            String SQL_UPDATE = "UPDATE movimiento_inventarios SET cantidad = COALESCE(?, cantidad), motivo = COALESCE(?, motivo), fecha = COALESCE(?, fecha) WHERE id = ?";
            try (PreparedStatement psUpdate = connection.prepareStatement(SQL_UPDATE)) {
                if (dto.cantidad != null) psUpdate.setInt(1, dto.cantidad); else psUpdate.setNull(1, java.sql.Types.INTEGER);
                if (dto.motivo != null) psUpdate.setString(2, dto.motivo); else psUpdate.setNull(2, java.sql.Types.VARCHAR);
                if (dto.fecha != null) psUpdate.setTimestamp(3, Timestamp.valueOf(dto.fecha + " 00:00:00")); else psUpdate.setNull(3, java.sql.Types.TIMESTAMP);
                psUpdate.setLong(4, dto.id);
                int filas = psUpdate.executeUpdate();
                if (filas == 0) {
                    connection.rollback();
                    return "Error: no se pudo actualizar el movimiento";
                }
            }

            if (dto.cantidad != null) {
                String SQL_UPDATE_STOCK = "UPDATE productos SET stock_actual = ? WHERE id = ?";
                try (PreparedStatement psStock = connection.prepareStatement(SQL_UPDATE_STOCK)) {
                    psStock.setInt(1, nuevoStock);
                    psStock.setLong(2, productoId);
                    int fu = psStock.executeUpdate();
                    if (fu == 0) {
                        connection.rollback();
                        return "Error: no se pudo actualizar el stock del producto";
                    }
                }
            }

            connection.commit();
            if (dto.cantidad != null) return "OK: Movimiento actualizado. Nuevo stock=" + nuevoStock;
            return "OK: Movimiento actualizado.";
        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}
