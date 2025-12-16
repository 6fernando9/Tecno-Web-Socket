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

public class ConsultarBarberoDTO {

    public LocalDateTime fecha;

    public ConsultarBarberoDTO(LocalDateTime fechaEntrante) {
        this.fecha = fechaEntrante;
    }

    public static Resultado<ConsultarBarberoDTO> crearMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        System.out.println(data.toString());
        if (data.length < 1) {
            return Resultado.error("Error: se esperaban al menos 1 campos (fecha)");
        }
        String fechaEntrante = data[0];

        if (GeneralMethods.esCampoNuloVacio(fechaEntrante)) {
            return Resultado.error("Error: el campo 'fechaEntrante' no puede ser nulo o vacio");
        }

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("d-M-yyyy HH:mm");
        try {
            LocalDateTime fechaValidada = LocalDateTime.parse(fechaEntrante, formatoFecha);
            if (fechaValidada.isBefore(LocalDateTime.now())) {
                return Resultado.error("Error: La fecha y hora de la cita no puede ser anterior a este momento");
            }
            return Resultado.ok(new ConsultarBarberoDTO(fechaValidada));
        } catch (DateTimeParseException e) {
            return Resultado.error("Error: El formato de fecha/hora debe ser d-m-Y HH:mm (ej: 16-12-2025 18:00)");
        }
    }
}
