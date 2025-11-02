package Backend.Pagos.ListPagos;

import Database.PGSQLClient;

import java.math.BigDecimal;
import java.sql.*;

public class ListarPagoDeVentaSQLQuery {

    private static final String BASE_QUERY = """
        SELECT 
            dp.id AS id_detalle,
            dp.monto,
            tp.nombre AS tipo_pago
        FROM public.detalle_pagos dp
        JOIN public.tipo_pagos tp ON dp.tipo_pago_id = tp.id
        WHERE dp.venta_id = ?
        ORDER BY dp.id ASC
        """;

    public String executeListarPagosDeVentaQuery(PGSQLClient pgsqlClient, long ventaId) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword());
             PreparedStatement ps = connection.prepareStatement(BASE_QUERY)) {

            ps.setLong(1, ventaId);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder result = new StringBuilder();

                while (rs.next()) {
                    result.append(formatearPago(rs));
                }

                if (result.length() == 0) {
                    return "No existen pagos registrados para la venta ID " + ventaId;
                }

                return result.toString();
            }

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }

    private String formatearPago(ResultSet rs) throws SQLException {
        long idDetalle = rs.getLong("id_detalle");
        BigDecimal monto = rs.getBigDecimal("monto");
        String tipoPago = rs.getString("tipo_pago");

        return String.format(
                "======================== DETALLE PAGO ======================\r\n" +
                        "id_detalle: %d\r\n" +
                        "monto: %s\r\n" +
                        "tipo_pago: %s\r\n" +
                        "============================================================\r\n",
                idDetalle, monto, tipoPago
        );
    }
}
