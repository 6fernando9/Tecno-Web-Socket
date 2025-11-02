package Backend.Pagos;

import Backend.Pagos.dto.PagoDTO;
import Backend.Pagos.dto.TipoPagoDTO;
import Backend.Pagos.dto.VentaSimpleDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneralPagoSQLUtils {

    private static final String SQL_FIND_VENTA = """
        SELECT 
            id, 
            monto_total, 
            estado_pago AS estado, 
            tipo_pago, 
            TO_CHAR(fecha_hora, 'YYYY-MM-DD HH24:MI:SS') AS fecha
        FROM public.ventas
        WHERE id = ?
        """;

    private static final String SQL_LIST_PAGOS = """
        SELECT 
            dp.id, 
            dp.monto, 
            tp.nombre AS tipo_pago
        FROM public.detalle_pagos dp
        JOIN public.tipo_pagos tp ON dp.tipo_pago_id = tp.id
        WHERE dp.venta_id = ?
        ORDER BY dp.id ASC
        """;
    private static final String SQL_FIND_TIPO_PAGO = """
            SELECT
                tp.id,
                tp.nombre
            FROM public.tipo_pagos tp
            WHERE tp.nombre = ?
            """;
    private static final String[] PAGOS_ACEPTADOS = {
            "contado","transferencia","tarjeta","qr","efectivo","stripe","pago facil"
    };

    public static VentaSimpleDTO findVentaConPagos(Connection connection, long ventaId) throws SQLException {
        VentaSimpleDTO venta = findVentaById(connection, ventaId);
        if (venta == null) {
            return null; // Venta no encontrada
        }

        List<PagoDTO> pagos = listarPagosDeVenta(connection, ventaId);
        venta.setListaPagos(pagos);
        return venta;
    }

    /**
     * Busca una venta por ID.
     */
    public static VentaSimpleDTO findVentaById(Connection connection, long ventaId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_VENTA)) {
            ps.setLong(1, ventaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new VentaSimpleDTO(
                            rs.getLong("id"),
                            rs.getFloat("monto_total"),
                            rs.getString("estado"),
                            rs.getString("tipo_pago"),
                            rs.getString("fecha")
                    );
                }
                return null;
            }
        }
    }
    public static float calcularMontoTotalDePago(VentaSimpleDTO ventaSimpleDTO){
        float totalPagado = 0f;
        if (ventaSimpleDTO.getListaPagos() != null) {
            totalPagado = (float) ventaSimpleDTO.getListaPagos().stream()
                    .mapToDouble(p -> p.monto)
                    .sum();
        }
        return totalPagado;
    }
    public static TipoPagoDTO findTipoPagoByName(Connection connection, String nombreTipoPago) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_TIPO_PAGO)) {
            ps.setString(1, nombreTipoPago);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TipoPagoDTO(
                            rs.getLong("id"),
                            rs.getString("nombre")
                    );
                }
                return null;
            }
        }
    }
    /**
     * Lista los pagos asociados a una venta.
     */
    public static List<PagoDTO> listarPagosDeVenta(Connection connection, long ventaId) throws SQLException {
        List<PagoDTO> pagos = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(SQL_LIST_PAGOS)) {
            ps.setLong(1, ventaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PagoDTO pago = new PagoDTO(
                            rs.getLong("id"),
                            rs.getFloat("monto"),
                            rs.getString("tipo_pago")
                    );
                    pagos.add(pago);
                }
            }
        }

        return pagos;
    }
    public static boolean esTipoPagoAceptado(String tipoPago) {
        if (tipoPago == null) return false;
        return Arrays.asList(PAGOS_ACEPTADOS)
                .contains(tipoPago.toLowerCase().trim());
    }

}
