package Backend.Horarios.dto;

import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Utils.TecnoUtils;

public class HorarioUpdateDTO extends HorarioDTO{
    public Long horarioId;
    public HorarioUpdateDTO(){
        super();
    }
    public HorarioUpdateDTO(Long usuarioId,Long horarioId,String horaInicio,String horaFin){
        super(usuarioId,null,horaInicio,horaFin);
        this.horarioId = horarioId;
    }
    public static Resultado<HorarioUpdateDTO> createUpdateHorarioDtoFromSubject(String subject) {
        String[] data = TecnoUtils.procesarString(subject);
        if(data.length < 4){
            return Resultado.error("Error..se esperaba al menos 4 campos(barbero_id,horario_id,hora_inicio,hora_fin)");
        }
        String horarioId = data[1];
        if(GeneralMethods.esCampoNuloVacio(horarioId)){
            return Resultado.error("Error.. el horario id no puede ser nulo o vacio");
        }
        Long horarioIdDto;
        try{
            horarioIdDto = Long.parseLong(horarioId);
        }catch (NumberFormatException e){
            return Resultado.error("Error.. campo horario Id no es numerico");
        }
        //le asignaremos un dia falso, solo para gozar la validacion
        String subjectParaReutilizarCreate = String.format(
                "[\"%s\",\"%s\",\"%s\",\"%s\"]",
                data[0], "lunes" ,data[2], data[3]
        );
        Resultado<HorarioDTO> resultadoCreate = HorarioDTO.createHorarioDtoFromSubject(subjectParaReutilizarCreate);
        if(!resultadoCreate.esExitoso()){
            return Resultado.error(resultadoCreate.getError());
        }
        HorarioDTO horarioDTO = resultadoCreate.getValor();
        //para evitar inconsistencias lo seteo a null de nuevo
        horarioDTO.dia = null;
        return Resultado.ok(new HorarioUpdateDTO(horarioDTO.id,horarioIdDto,horarioDTO.horaInicio,horarioDTO.horaFin));
    }
}
