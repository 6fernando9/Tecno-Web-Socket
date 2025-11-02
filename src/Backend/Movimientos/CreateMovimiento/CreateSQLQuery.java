package Backend.Movimientos.CreateMovimiento;

import Backend.Movimientos.dto.CreateMovimientoDTO;
import Backend.Productos.GeneralProductoSQLUtils;
import Backend.Productos.dto.UpdateProductoDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateSQLQuery {
    private static final String SQL_INSERT =
            "INSERT INTO movimiento_inventarios (producto_id, usuario_id, tipo_movimiento, cantidad, fecha, motivo) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";

    public String executeInsertMovimientoAndApplyStock(PGSQLClient pgsqlClient, CreateMovimientoDTO dto) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");
            connection.setAutoCommit(false);

            // comprobar que el producto existe y obtener stock
            UpdateProductoDTO producto = GeneralProductoSQLUtils.findProductoById(connection, dto.productoId);
            if (producto == null) {
                return "Error: El producto no existe (id=" + dto.productoId + ")";
            }

            int nuevoStock = producto.stockActual;
            if (dto.tipoMovimiento.equals("ingreso")) {
                nuevoStock += dto.cantidad;
            } else if (dto.tipoMovimiento.equals("salida_venta")) {
                if (producto.stockActual < dto.cantidad) {
                    return "Error: Stock insuficiente. Stock actual=" + producto.stockActual + " cantidad solicitada=" + dto.cantidad;
                }
                nuevoStock -= dto.cantidad;
            } else if (dto.tipoMovimiento.equals("ajuste")) {
                // Para ajuste, asumimos que cantidad puede representar un delta positivo (sumar) o se puede
                // acordar que para restar se envíe la cantidad y un motivo indicando -; aquí tratamos como suma positiva
                nuevoStock += dto.cantidad;
            }

            // insertar movimiento
            try (PreparedStatement psInsert = connection.prepareStatement(SQL_INSERT)) {
                psInsert.setLong(1, dto.productoId);
                psInsert.setLong(2, dto.usuarioId);
                psInsert.setString(3, dto.tipoMovimiento);
                psInsert.setInt(4, dto.cantidad);
                psInsert.setString(5, dto.motivo);
                int filas = psInsert.executeUpdate();
                if (filas == 0) {
                    connection.rollback();
                    return "Error: no se pudo insertar el movimiento";
                }
            }

            // actualizar stock
            String SQL_UPDATE_STOCK = "UPDATE productos SET stock_actual = ? WHERE id = ?";
            try (PreparedStatement psUpdate = connection.prepareStatement(SQL_UPDATE_STOCK)) {
                psUpdate.setInt(1, nuevoStock);
                psUpdate.setLong(2, dto.productoId);
                int filasU = psUpdate.executeUpdate();
                if (filasU == 0) {
                    connection.rollback();
                    return "Error: no se pudo actualizar stock del producto";
                }
            }

            connection.commit();
            return "OK: Movimiento registrado. Nuevo stock=" + nuevoStock;
        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}
