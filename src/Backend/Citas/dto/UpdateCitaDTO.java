package Backend.Citas.dto;

import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UpdateCitaDTO {
    public long citaId;
    public Long clienteId; // id del cliente que solicita la actualización
    public Long barberoId; // nuevo barbero (opcional)
    public LocalDateTime fecha; // ISO: YYYY-MM-DDTHH:MM (opcional)


    public UpdateCitaDTO() {}

    public UpdateCitaDTO(long citaId, Long clienteId, Long barberoId, LocalDateTime fecha) {
        this.citaId = citaId;
        this.clienteId = clienteId;
        this.barberoId = barberoId;
        this.fecha = fecha;
    }

    // Subject expected: cita_update["citaId","usuarioId","barberoId","2025-11-05T10:00","2025-11-05T10:30"]
    // Use empty string "" for optional fields
    public static Resultado<UpdateCitaDTO> crearMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);

        // Validamos que existan los 4 parámetros (citaId, clienteId, barberoId, fecha)
        if (data.length < 4) {
            return Resultado.error("Error: se esperaban al menos 4 campos (citaId, clienteId, barberoId, fecha)");
        }

        String citaIdEntrante = data[0];
        String clienteIdEntrante = data[1];
        String barberoIdEntrante = data[2];
        String fechaEntrante = data[3];

        // Validaciones de nulidad básicas
        if (GeneralMethods.esCampoNuloVacio(citaIdEntrante)) return Resultado.error("Error: el campo 'citaId' no puede ser nulo");
        if (GeneralMethods.esCampoNuloVacio(clienteIdEntrante)) return Resultado.error("Error: el campo 'clienteId' no puede ser nulo");
        if (GeneralMethods.esCampoNuloVacio(barberoIdEntrante)) return Resultado.error("Error: el campo 'barberoId' no puede ser nulo");
        if (GeneralMethods.esCampoNuloVacio(fechaEntrante)) return Resultado.error("Error: el campo 'fecha' no puede ser nulo");

        long citaIdDto;
        Long clienteIdDto;
        Long barberoIdDto;
        LocalDateTime fechaValidada;

        try {
            citaIdDto = Long.parseLong(citaIdEntrante.trim());
            clienteIdDto = Long.parseLong(clienteIdEntrante.trim());
            barberoIdDto = Long.parseLong(barberoIdEntrante.trim());

            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("d-M-yyyy HH:mm");
            fechaValidada = LocalDateTime.parse(fechaEntrante.trim(), formatoFecha);

            if (fechaValidada.isBefore(LocalDateTime.now())) {
                return Resultado.error("Error: La nueva fecha no puede ser anterior al momento actual");
            }

            return Resultado.ok(new UpdateCitaDTO(citaIdDto, clienteIdDto, barberoIdDto, fechaValidada));

        } catch (NumberFormatException e) {
            return Resultado.error("Error: Los IDs deben ser valores numéricos válidos");
        } catch (DateTimeParseException e) {
            return Resultado.error("Error: El formato de fecha/hora debe ser d-m-Y HH:mm (ej: 16-12-2025 18:00)");
        }
    }
}
