package Backend.Pagos.dto;

import Backend.Utils.GeneralMethods.Resultado;
import Utils.TecnoUtils;

public class UpdatePagoDTO extends PagoDTO{
    public Long pagoId;
    public UpdatePagoDTO(){
        super();
    }
    public UpdatePagoDTO(Long ventaId,Long pagoId, float monto,String tipoPago){
        super(ventaId,monto,tipoPago);
        this.pagoId = pagoId;
    }
    public static Resultado<UpdatePagoDTO> createUpdatePagoDto(String subject){
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 4) {
            return Resultado.error("Error.. se esperaba al menos 4 parametros(venta_id,pago_id,monto,tipo_pago)");
        }
        String pagoId = data[1] ;
        if(pagoId == null || pagoId.equalsIgnoreCase("null")){
            return Resultado.error("Error.. el pagoId no puede ser nulo");
        }
        Long pagoIdDto;
        try{
            pagoIdDto = Long.parseLong(pagoId);
        }catch (NumberFormatException e){
            return Resultado.error("Error.. el campo pagoId no es numerico..");
        }
        String subjectParaReutilizarCreate = String.format(
                "[\"%s\",\"%s\",\"%s\"]",
                data[0], data[2], data[3]
        );
        Resultado<PagoDTO> resultadoCreate = PagoDTO.crearPagoFromSubject(subjectParaReutilizarCreate);
        if (!resultadoCreate.esExitoso()) {
            return Resultado.error(resultadoCreate.getError());
        }
        PagoDTO pagoDTO = resultadoCreate.getValor();
        return Resultado.ok(new UpdatePagoDTO(pagoDTO.id,pagoIdDto,pagoDTO.monto,pagoDTO.tipoPago));
    }
}
