package Backend.Movimientos.dto;

import Backend.Movimientos.GeneralMovimientoUtils;
import Backend.TipoMovimiento;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class CreateMovimientoDTO {
    public long productoId;

    public String tipoMovimiento; // ingreso | salida_venta | ajuste
    public int cantidad;
    public String motivo;

    public CreateMovimientoDTO(){}

    public CreateMovimientoDTO(long productoId, String tipoMovimiento, int cantidad, String motivo){
        this.productoId = productoId;
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
        String productoIdEntrante = data[0];
        String tipoEntrante = data[1];
        String cantidadEntrante = data[2];
        String motivoEntrante = data[3];
        if(GeneralMethods.esCampoNuloVacio(productoIdEntrante)){
            return Resultado.error("Error..el producto id no puede ser nulo");
        }
        if(GeneralMethods.esCampoNuloVacio(tipoEntrante)){
            return Resultado.error("Error..el tipo entrante no puede ser nulo");
        }
        if(GeneralMethods.esCampoNuloVacio(cantidadEntrante)){
            return Resultado.error("Error..la cantidad entrante no puede ser nulo");
        }

        long productoId;


        int cantidad;

        try {
            productoId = Long.parseLong(productoIdEntrante);
        } catch (Exception e) {
            return Resultado.error("Error: producto_id inválido");
        }

        if (!GeneralMovimientoUtils.esTipoMovimientoPermitido(tipoEntrante)) {
            return Resultado.error("Error: tipo_movimiento debe ser 'entrada', 'salida' o 'ajuste'");
        }

        try {
            cantidad = Integer.parseInt(cantidadEntrante);
        } catch (Exception e) {
            return Resultado.error("Error: cantidad inválida");
        }
        if(tipoEntrante.equalsIgnoreCase(TipoMovimiento.AJUSTE.getDescripcion())){
            if (cantidad == 0) {
                return Resultado.error("Error: la cantidad debe ser diferente de 0");
            }
        }else{
            //en el caso de que sea entrada o saliente
            System.out.println("cantidad por else " + cantidad);
            if (cantidad <= 0) {
                return Resultado.error("Error: la cantidad debe ser mayor a 0");
            }
        }

        String motivoDto = motivoEntrante.trim().equalsIgnoreCase("null") ? null : motivoEntrante;

        CreateMovimientoDTO dto = new CreateMovimientoDTO(productoId, tipoEntrante, cantidad, motivoDto);
        return Resultado.ok(dto);
    }

    @Override
    public String toString() {
        return "Movimiento { productoId=" + productoId + ", tipo=" + tipoMovimiento + ", cantidad=" + cantidad + ", motivo=" + motivo + " }";
    }

    public String toStringCorreo() {
        return "Movimiento creado {\r\n" +
                "  productoId = '" + productoId + "'\r\n" +
                "  tipo = '" + tipoMovimiento + "'\r\n" +
                "  cantidad = '" + cantidad + "'\r\n" +
                "  motivo = '" + (motivo == null ? "" : motivo) + "'\r\n" +
                "}";
    }
}
