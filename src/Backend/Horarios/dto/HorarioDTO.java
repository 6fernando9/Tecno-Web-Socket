package Backend.Horarios.dto;

import Backend.Horarios.GeneralHorarioSQL;
import Backend.Utils.GeneralMethods.Resultado;
import Utils.TecnoUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class HorarioDTO {
    public Long id;
    public String dia;
    public String horaInicio;
    public String horaFin;
    public HorarioDTO(){}
    public HorarioDTO(Long id, String dia, String horaInicio,String horaFin){
        this.id = id;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }
    public static Resultado<HorarioDTO> createHorarioDtoFromSubject(String subject){
        String[] data = TecnoUtils.procesarString(subject);
        if(data.length < 4){
            return Resultado.error("Error.. se esperaba al menos 4 campos(barbero_id,dia,hora_inicio,hora_fin)");
        }
        String id = data[0];
        String dia = data[1];
        String horaInicio = data[2];
        String horaFin = data[3];
        Long idDto;

        if(id == null || id.equalsIgnoreCase("null")){
            return Resultado.error("Error.. el campo id no puede ser nulo");
        }
        if(dia == null || dia.equalsIgnoreCase("null")){
            return Resultado.error("Error.. el campo dia no puede ser null");
        }
        if(horaInicio == null || horaInicio.equalsIgnoreCase("null")){
            return Resultado.error("Error.. el campo hora inicio no puede ser null");
        }
        if(horaFin == null || horaFin.equalsIgnoreCase("null")){
            return Resultado.error("Error.. el campo hora fin no puede ser null");
        }
        try{
            idDto = Long.parseLong(id);
        }catch (NumberFormatException e){
            return Resultado.error("Error.. ocurrio un error inesperado al convertir las fechas");
        }

        //solo validacion de fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            LocalTime inicio = LocalTime.parse(horaInicio, formatter);
            LocalTime fin = LocalTime.parse(horaFin, formatter);

            if (inicio.isAfter(fin)) {
                return Resultado.error("Error: la hora de inicio no puede ser posterior a la hora de fin.");
            }

        } catch (DateTimeParseException e) {
            return Resultado.error("Error: formato de hora inválido. Use formato HH:mm (por ejemplo 10:30).");
        }
        if (!GeneralHorarioSQL.esDiaValido(dia)) {
            return Resultado.error("Error.. el dia no es valido");
        }
        return Resultado.ok(new HorarioDTO(idDto,dia,horaInicio,horaFin));
    }
    @Override
    public String toString() {
        return "HorarioDTO{" +
                "id=" + id +
                ", dia='" + dia + '\'' +
                ", horaInicio='" + horaInicio + '\'' +
                ", horaFin='" + horaFin + '\'' +
                '}';
    }
}
