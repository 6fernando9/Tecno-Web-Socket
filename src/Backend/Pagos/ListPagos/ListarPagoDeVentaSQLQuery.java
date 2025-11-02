package Backend.Pagos.ListPagos;

import Backend.Pagos.GeneralPagoSQLUtils;
import Backend.Pagos.dto.PagoDTO;
import Backend.Pagos.dto.VentaSimpleDTO;
import Database.PGSQLClient;
import java.sql.*;

public class ListarPagoDeVentaSQLQuery {


    public String executeListarPagosDeVentaQuery(PGSQLClient pgsqlClient, long ventaId) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            VentaSimpleDTO venta = GeneralPagoSQLUtils.findVentaConPagos(connection, ventaId);

            if (venta == null) {
                return "No existe una venta con ID " + ventaId;
            }
            StringBuilder result = new StringBuilder();
            //result.append(formatearVenta(venta));

            if (venta.getListaPagos() == null || venta.getListaPagos().isEmpty()) {
                result.append("No hay pagos registrados para esta venta.");
            } else {
                result.append("========= PAGOS ASOCIADOS =========\r\n");
                for (PagoDTO pago : venta.getListaPagos()) {
                    result.append(formatearPago(pago));
                }
                result.append("===================================\r\n");
            }

            return result.toString();

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

    // =========================================================
    // MÉTODOS DE FORMATEO
    // =========================================================
    private String formatearVenta(VentaSimpleDTO venta) {
        return String.format(
                "======================== VENTA ========================\r\n" +
                        "id: %d\r\n" +
                        "monto_total: %.2f\r\n" +
                        "estado: %s\r\n" +
                        "tipo_pago: %s\r\n" +
                        "fecha: %s\r\n" +
                        "========================================================\r\n",
                venta.id, venta.montoTotal, venta.estado, venta.tipoPago, venta.fecha
        );
    }

    private String formatearPago(PagoDTO pago) {
        return String.format(
                "id_pago: %d\r\n" +
                        "monto: %.2f\r\n" +
                        "tipo_pago: %s\r\n" +
                        "-------------------------------------------------------\r\n",
                pago.id, pago.monto, pago.tipoPago
        );
    }
}
