package Backend.Movimientos.UpdateMovimiento;

import Backend.Movimientos.GeneralMovimientoUtils;
import Backend.Movimientos.dto.MovimientoDTO;
import Backend.Movimientos.dto.UpdateMovimientoDTO;
import Backend.Productos.GeneralProductoSQLUtils;
import Backend.Productos.dto.UpdateProductoDTO;
import Backend.TipoMovimiento;
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
            MovimientoDTO movimientoDTO = GeneralMovimientoUtils.findMovimientoConProductoById(connection,dto.id);
            if(movimientoDTO == null){
                return "Error: Movimiento no encontrado (ID: " + dto.id + ")";
            }
            String sql = "UPDATE movimiento_inventarios SET motivo = ?, fecha = ? WHERE id = ?";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, dto.motivo);
                    ps.setTimestamp(2, Timestamp.valueOf(dto.fecha));
                    ps.setLong(3, dto.id);
                    int filasAfectadas = ps.executeUpdate();
                    if (filasAfectadas > 0) {
                        return "OK: Movimiento actualizado (ID: " + dto.id + ")";
                    } else {
                        return "Error: No se encontró el movimiento para actualizar.";
                    }
                }
        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
    public String executeAnularMovimiento(PGSQLClient pgsqlClient, long movimientoId) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            connection.setAutoCommit(false);

            MovimientoDTO mov = GeneralMovimientoUtils.findMovimientoConProductoById(connection, movimientoId);

            if (mov == null) {
                return "Error: El movimiento con ID " + movimientoId + " no existe.";
            }
            if(mov.estado.equalsIgnoreCase("anulado")){
                return "El movimiento ya esta anulado";
            }
            int stockActual = mov.producto.stockActual;
            int cantidadMovimiento = mov.cantidad;
            int nuevoStock = stockActual;
            if(mov.tipoMovimiento.equalsIgnoreCase(TipoMovimiento.ENTRADA.getDescripcion())){
                nuevoStock -= cantidadMovimiento;
            }else if(mov.tipoMovimiento.equalsIgnoreCase(TipoMovimiento.SALIDA.getDescripcion())){
                nuevoStock += cantidadMovimiento;
            }else if(mov.tipoMovimiento.equalsIgnoreCase(TipoMovimiento.AJUSTE.getDescripcion())){
                nuevoStock -= cantidadMovimiento;
            }else{
                connection.rollback();
                return "Error: Tipo de movimiento desconocido.";
            }
            if (nuevoStock < 0) {
                connection.rollback();
                return "Error: No se puede anular. El stock resultante sería negativo (" + nuevoStock + "). " +
                        "Ajuste el inventario físicamente antes de anular.";
            }

            String sqlAnular = "UPDATE movimiento_inventarios SET estado = 'anulado' WHERE id = ?";
            try (PreparedStatement psMov = connection.prepareStatement(sqlAnular)) {
                psMov.setLong(1, movimientoId);
                psMov.executeUpdate();
            }

            String sqlStock = "UPDATE productos SET stock_actual = ? WHERE id = ?";
            try (PreparedStatement psProd = connection.prepareStatement(sqlStock)) {
                psProd.setInt(1, nuevoStock);
                psProd.setLong(2, mov.producto.id);
                psProd.executeUpdate();
            }
            connection.commit();
            return "OK: Movimiento anulado. Stock revertido de " + stockActual + " a " + nuevoStock;

        } catch (Exception e) {
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}
