package Backend.Movimientos.dto;

import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UpdateMovimientoDTO {
    public long id;
    public String motivo; // nullable
    public LocalDateTime fecha; // nullable, expected format YYYY-MM-DD

    public UpdateMovimientoDTO() {}

    public UpdateMovimientoDTO(long id, String motivo, LocalDateTime fecha) {
        this.id = id;
        this.motivo = motivo;
        this.fecha = fecha;
    }

    public static Resultado<UpdateMovimientoDTO> crearMedianteSubject(String subject) {
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 3) {
            return Resultado.error("Error: se esperaban al menos 3 parámetros (idMovimiento, motivo, fecha)");
        }
        String idEntrante = data[0];
        String motivoEntrante = data[1];
        String fechaEntrante = data[2];
        if (GeneralMethods.esCampoNuloVacio(idEntrante)) {
            return Resultado.error("Error: el ID del movimiento es obligatorio.");
        }
        if (GeneralMethods.esCampoNuloVacio(fechaEntrante)) {
            return Resultado.error("Error: la fecha no puede estar vacía.");
        }

        try {
            long id = Long.parseLong(idEntrante);
            String motivoDto = motivoEntrante.equalsIgnoreCase("null") ? null : motivoEntrante;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime fechaHora = LocalDateTime.parse(fechaEntrante, formatter);

            return Resultado.ok(new UpdateMovimientoDTO(id, motivoDto, fechaHora));

        } catch (NumberFormatException e) {
            return Resultado.error("Error: El ID del movimiento debe ser un número válido.");
        } catch (DateTimeParseException e) {
            return Resultado.error("Error: El formato de fecha debe ser dd-MM-yyyy HH:mm (Ej: 17-12-2025 14:30)");
        } catch (Exception e) {
            return Resultado.error("Error inesperado: " + e.getMessage());
        }
    }
}
