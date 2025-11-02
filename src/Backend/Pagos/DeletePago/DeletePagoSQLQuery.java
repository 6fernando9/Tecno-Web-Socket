package Backend.Pagos.DeletePago;

import Backend.Pagos.GeneralPagoSQLUtils;
import Backend.Pagos.dto.VentaSimpleDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DeletePagoSQLQuery {

    private static final String SQL_EXISTE_PAGO = """
        SELECT COUNT(*) AS cantidad
        FROM public.detalle_pagos
        WHERE id = ? AND venta_id = ?
        """;

    private static final String SQL_DELETE_PAGO = """
        DELETE FROM public.detalle_pagos
        WHERE id = ? AND venta_id = ?
        """;

    private static final String SQL_UPDATE_VENTA_ESTADO = """
        UPDATE public.ventas
        SET estado_pago = ?
        WHERE id = ?
        """;

    public String executeEliminarPago(PGSQLClient pgsqlClient, long ventaId, long pagoId) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Conectado correctamente a la base de datos");
            boolean existe = false;
            try (PreparedStatement ps = connection.prepareStatement(SQL_EXISTE_PAGO)) {
                ps.setLong(1, pagoId);
                ps.setLong(2, ventaId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        existe = rs.getInt("cantidad") > 0;
                    }
                }
            }

            if (!existe) {
                return "No existe un pago con ID " + pagoId + " asociado a la venta " + ventaId;
            }
            int filasEliminadas = 0;
            try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE_PAGO)) {
                ps.setLong(1, pagoId);
                ps.setLong(2, ventaId);
                filasEliminadas = ps.executeUpdate();
            }

            if (filasEliminadas == 0) {
                return "No se eliminó ningún registro. Verifica el ID del pago.";
            }

            // 3️⃣ Recalcular el total de pagos restantes
            VentaSimpleDTO ventaSimpleDTO = GeneralPagoSQLUtils.findVentaConPagos(connection, ventaId);
            if (ventaSimpleDTO == null) {
                return "La venta con ID " + ventaId + " no existe o fue eliminada.";
            }

            float totalPagado = GeneralPagoSQLUtils.calcularMontoTotalDePago(ventaSimpleDTO);
            String nuevoEstado;
            if (totalPagado == 0) {
                nuevoEstado = "pendiente_saldo";
            } else if (totalPagado < ventaSimpleDTO.montoTotal) {
                nuevoEstado = "pendiente_saldo";
            } else {
                nuevoEstado = "pagado";
            }

            try (PreparedStatement psUpdate = connection.prepareStatement(SQL_UPDATE_VENTA_ESTADO)) {
                psUpdate.setString(1, nuevoEstado);
                psUpdate.setLong(2, ventaId);
                psUpdate.executeUpdate();
            }

            return "Pago eliminado correctamente. Total restante abonado: " + totalPagado +
                    " / " + ventaSimpleDTO.montoTotal +
                    ". Estado de la venta: " + nuevoEstado;

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}
