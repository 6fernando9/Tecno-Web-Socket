package Backend.Citas.dto;

import Backend.Utils.GeneralMethods.Resultado;
import Utils.TecnoUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ListarCitaDTO {
    public Long clienteId;
    public Long barberoId;
    public String estado;
    public LocalDateTime fechaInicio;
    public LocalDateTime fechaFin;
    public Long servicioId;

    public ListarCitaDTO() {}

    public static Resultado<ListarCitaDTO> crearMedianteSubject(String subject) {
        String[] data = TecnoUtils.procesarString(subject);

        if (data.length < 6) {
            return Resultado.error("Error: Se esperaban 7 atributos [clienteId, barberoId, estado, fechaInicio, fechaFin, servicioId, limit]");
        }

        ListarCitaDTO dto = new ListarCitaDTO();
        DateTimeFormatter soloFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter fechaHora = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        try {
            // IDs y Cadenas Simples
            String clienteTemp = obtenerNuloOValor(data[0]);
            if (clienteTemp != null) dto.clienteId = Long.parseLong(clienteTemp);

            String barberoTemp = obtenerNuloOValor(data[1]);
            if (barberoTemp != null) dto.barberoId = Long.parseLong(barberoTemp);

            dto.estado = obtenerNuloOValor(data[2]);

            // Lógica de Fechas Inteligente
            String inicioTemp = obtenerNuloOValor(data[3]);
            if (inicioTemp != null) {
                dto.fechaInicio = inicioTemp.contains(" ")
                        ? LocalDateTime.parse(inicioTemp, fechaHora)
                        : LocalDate.parse(inicioTemp, soloFecha).atTime(LocalTime.MIN);
            }

            String finTemp = obtenerNuloOValor(data[4]);
            if (finTemp != null) {
                dto.fechaFin = finTemp.contains(" ")
                        ? LocalDateTime.parse(finTemp, fechaHora)
                        : LocalDate.parse(finTemp, soloFecha).atTime(LocalTime.MAX);
            }

            String servicioTemp = obtenerNuloOValor(data[5]);
            if (servicioTemp != null) dto.servicioId = Long.parseLong(servicioTemp);


            return Resultado.ok(dto);

        } catch (NumberFormatException e) {
            return Resultado.error("Error: Los IDs y el Límite deben ser números válidos.");
        } catch (DateTimeParseException e) {
            return Resultado.error("Error: Formato de fecha inválido. Use dd-MM-yyyy o dd-MM-yyyy HH:mm.");
        }
    }

    private static String obtenerNuloOValor(String valor) {
        if (valor == null || valor.trim().isEmpty() || valor.trim().equalsIgnoreCase("null")) {
            return null;
        }
        return valor.trim();
    }

    @Override
    public String toString() {
        return "ListCitaDTO{cliente=" + clienteId + ", barbero=" + barberoId + ", estado='" + estado +
                "', inicio=" + fechaInicio + ", fin=" + fechaFin + ", servicio=" + servicioId + "}";
    }
}