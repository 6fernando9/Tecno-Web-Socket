package Backend.Citas.dto;

import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConsultarServicioBarberoDTO {
    public Long barberoId;
    public Set<Long> serviciosIds;
    public ConsultarServicioBarberoDTO(){}
    public ConsultarServicioBarberoDTO(Long barberoId,Set<Long> serviciosIds){
        this.barberoId = barberoId;
        this.serviciosIds = serviciosIds;
    }
    public static Resultado<ConsultarServicioBarberoDTO> crearMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarStringSeguro(subject);
        System.out.println(data.toString());
        if (data.length < 2) {
            return Resultado.error("Error: se esperaban al menos 2 campos (barbero_id,servicios_ids)");
        }
        String barberoIdEntrante = data[0];
        String serviciosEntrantes = data[1];
        Long barberoIdDto;
        if (GeneralMethods.esCampoNuloVacio(barberoIdEntrante)) {
            return Resultado.error("Error: el campo 'barberoId' no puede ser nulo o vacio");
        }
        if (GeneralMethods.esCampoNuloVacio(serviciosEntrantes)) {
            return Resultado.error("Error: el campo 'servicios' no puede ser nulo o vacio");
        }
        try {
            barberoIdDto = Long.parseLong(barberoIdEntrante);
        } catch (Exception e) {
            return Resultado.error("Error: barberoId inválido");
        }
        String[] serviciosSplit = serviciosEntrantes.split(",");
        Set<Long> serviciosSet = Arrays.stream(serviciosSplit)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toSet());

        if (serviciosSet.isEmpty()) {
            return Resultado.error("Error: La lista de Servicios está vacía o es inválida.");
        }

        return Resultado.ok(new ConsultarServicioBarberoDTO(barberoIdDto,serviciosSet));
    }

}
