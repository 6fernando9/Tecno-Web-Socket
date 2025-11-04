package Backend.Pagos.dto;

import Backend.Pagos.GeneralPagoSQLUtils;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Utils.TecnoUtils;

public class PagoDTO {
    public Long id;
    public float monto;
    public String tipoPago;
    public PagoDTO(){}
    public PagoDTO(Long id,float monto, String tipoPago){
        this.id = id;
        this.monto = monto;
        this.tipoPago = tipoPago;
    }
    @Override
    public String toString() {
        return "PagoDTO{" +
                "id=" + id +
                ", monto=" + monto +
                ", tipoPago='" + tipoPago + '\'' +
                '}';
    }
    public static Resultado<PagoDTO> crearPagoFromSubject(String subject){
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 3) {
            return Resultado.error("Error.. se esperaba al menos 3 datos (venta_id,tipoPago,monto)");
        }
        String ventaId = data[0];
        String tipoPago = data[1];
        String monto = data[2];

        if (GeneralMethods.esCampoNuloVacio(ventaId)) {
            return Resultado.error("Error.. el campo de venta no puede ser nulo o vacio");
        }
        if (GeneralMethods.esCampoNuloVacio(tipoPago)) {
            return Resultado.error("Error.. el campo de tipo de pago no puede ser nulo o vacio");
        }
        if (GeneralMethods.esCampoNuloVacio(monto)) {
            return Resultado.error("Error.. el campo de monto no puede ser nulo o vacio");
        }
        Long ventaIdDto;
        float montoDto;
        try{
            ventaIdDto = Long.parseLong(ventaId);
            montoDto = Float.parseFloat(monto);
        }catch (NumberFormatException e){
            return Resultado.error("Error.. hubo un error al momento de convertir los datos");
        }
        if (montoDto <= 0) {
            return Resultado.error("Error.. el monto a pagar debe ser mayor a 0");
        }
        if (!GeneralPagoSQLUtils.esTipoPagoAceptado(tipoPago)) {
            return Resultado.error("Error.. el tipo de pago ingresado no es valido");
        }
        return Resultado.ok(new PagoDTO(ventaIdDto,montoDto,tipoPago));
    }
}
