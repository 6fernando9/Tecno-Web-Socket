package Backend.Movimientos.dto;

import Backend.Productos.GeneralProductoSQLUtils;
import Backend.Usuarios.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class CreateMovimientoDTO {
    public long productoId;
    public long usuarioId;
    public String tipoMovimiento; // ingreso | salida_venta | ajuste
    public int cantidad;
    public String motivo;

    public CreateMovimientoDTO(){}

    public CreateMovimientoDTO(long productoId, long usuarioId, String tipoMovimiento, int cantidad, String motivo){
        this.productoId = productoId;
        this.usuarioId = usuarioId;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.motivo = motivo;
    }

    public static Resultado<CreateMovimientoDTO> crearMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        // Esperamos al menos 4 campos: producto_id, usuario_id, tipo_movimiento, cantidad
        if (data.length < 4) {
            return Resultado.error("Error: se esperaban al menos 4 campos (producto_id, usuario_id, tipo_movimiento, cantidad)");
        }

        long productoId;
        long usuarioId;
        String tipo = data[2];
        int cantidad;
        String motivo = null;

        try {
            productoId = Long.parseLong(data[0]);
        } catch (Exception e) {
            return Resultado.error("Error: producto_id inválido");
        }
        try {
            usuarioId = Long.parseLong(data[1]);
        } catch (Exception e) {
            return Resultado.error("Error: usuario_id inválido");
        }
        if (tipo == null || tipo.isEmpty()) {
            return Resultado.error("Error: tipo_movimiento no puede estar vacío");
        }
        tipo = tipo.toLowerCase();
        if (!(tipo.equals("ingreso") || tipo.equals("salida_venta") || tipo.equals("ajuste"))) {
            return Resultado.error("Error: tipo_movimiento debe ser 'ingreso', 'salida_venta' o 'ajuste'");
        }

        try {
            cantidad = Integer.parseInt(data[3]);
        } catch (Exception e) {
            return Resultado.error("Error: cantidad inválida");
        }
        if (cantidad <= 0) {
            return Resultado.error("Error: la cantidad debe ser mayor a 0");
        }

        if (data.length >= 5) {
            motivo = data[4];
        }

        CreateMovimientoDTO dto = new CreateMovimientoDTO(productoId, usuarioId, tipo, cantidad, motivo);
        return Resultado.ok(dto);
    }

    @Override
    public String toString() {
        return "Movimiento { productoId=" + productoId + ", usuarioId=" + usuarioId + ", tipo=" + tipoMovimiento + ", cantidad=" + cantidad + ", motivo=" + motivo + " }";
    }

    public String toStringCorreo() {
        return "Movimiento creado {\r\n" +
                "  productoId = '" + productoId + "'\r\n" +
                "  usuarioId = '" + usuarioId + "'\r\n" +
                "  tipo = '" + tipoMovimiento + "'\r\n" +
                "  cantidad = '" + cantidad + "'\r\n" +
                "  motivo = '" + (motivo == null ? "" : motivo) + "'\r\n" +
                "}";
    }
}
