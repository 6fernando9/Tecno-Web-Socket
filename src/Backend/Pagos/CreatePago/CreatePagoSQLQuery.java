package Backend.Pagos.CreatePago;

import Backend.Pagos.GeneralPagoSQLUtils;
import Backend.Pagos.dto.PagoDTO;
import Backend.Pagos.dto.TipoPagoDTO;
import Backend.Pagos.dto.VentaSimpleDTO;
import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class CreatePagoSQLQuery {
    private static final String INSERT_DETALLE_PAGO = """
        INSERT INTO public.detalle_pagos (monto, tipo_pago_id, venta_id)
        VALUES (?, ?, ?)
        """;

    private static final String UPDATE_VENTA_ESTADO = """
        UPDATE public.ventas
        SET estado_pago = ?
        WHERE id = ?
        """;
    //en este caso el pago dto el id es el de la venta
    public String executeCrearPago(PGSQLClient pgsqlClient, PagoDTO pagoDTO){
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");
            TipoPagoDTO tipoPagoDTO = GeneralPagoSQLUtils.findTipoPagoByName(connection,pagoDTO.tipoPago);
            if (tipoPagoDTO == null) {
                return "Error.. tipo de pago no soportado";
            }
            VentaSimpleDTO ventaSimpleDTO = GeneralPagoSQLUtils.findVentaConPagos(connection,pagoDTO.id);
            if (ventaSimpleDTO == null) {
                return "Error.. la venta no existe";
            }
            //calcular el total actual de pagos
            float nuevoTotal = GeneralPagoSQLUtils.calcularMontoTotalDePago(ventaSimpleDTO) + pagoDTO.monto;
            if (nuevoTotal > ventaSimpleDTO.montoTotal) {
                return "Error el monto del pago " + nuevoTotal +" supera al monto total de la venta " + ventaSimpleDTO.montoTotal;
            }

            try (PreparedStatement ps = connection.prepareStatement(INSERT_DETALLE_PAGO)) {
                ps.setFloat(1, pagoDTO.monto);
                ps.setLong(2, tipoPagoDTO.id);
                ps.setLong(3, ventaSimpleDTO.id);

                int filas = ps.executeUpdate();
                if (filas == 0) {
                    return "Error: no se pudo registrar el pago.";
                }
            }
            //para actualizar el estado
            String nuevoEstado = (nuevoTotal == ventaSimpleDTO.montoTotal)
                    ? "pagado"
                    : "pendiente_saldo";

            try (PreparedStatement psUpdate = connection.prepareStatement(UPDATE_VENTA_ESTADO)) {
                psUpdate.setString(1, nuevoEstado);
                psUpdate.setLong(2, ventaSimpleDTO.id);
                psUpdate.executeUpdate();
            }
            return "Pago registrado con éxito. Total abonado: " + nuevoTotal +
                    " / " + ventaSimpleDTO.montoTotal +
                    ". Estado de la venta: " + nuevoEstado;
        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

}
