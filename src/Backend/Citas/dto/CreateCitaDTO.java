package Backend.Citas.dto;

import Backend.Usuarios.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class CreateCitaDTO {
    public long clienteId;
    public Long barberoId; // optional
    public String serviciosCsv; // comma separated servicio ids
    public String fechaHoraInicio; // ISO: YYYY-MM-DDTHH:MM
    public String fechaHoraFin; // ISO: YYYY-MM-DDTHH:MM
    public String observaciones;
    public Double pagoInicial; // optional

    public CreateCitaDTO(){}

    public CreateCitaDTO(long clienteId, Long barberoId, String serviciosCsv, String fechaHoraInicio, String fechaHoraFin, String observaciones, Double pagoInicial){
        this.clienteId = clienteId;
        this.barberoId = barberoId;
        this.serviciosCsv = serviciosCsv;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.observaciones = observaciones;
        this.pagoInicial = pagoInicial;
    }

    // Subject expected: cita_create["clienteId","barberoId","serviciosCsv","2025-11-03T14:30","2025-11-03T15:00","observaciones","12.50"]
    // Use empty string "" for optional fields you want to skip (e.g., barberoId)
    public static Resultado<CreateCitaDTO> crearMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 4) {
            return Resultado.error("Error: se esperaban al menos 4 campos (clienteId, barberoId, serviciosCsv, fechaHoraInicio)");
        }

        long clienteId;
        Long barberoId = null;
        String serviciosCsv;
        String fechaInicio;
        String fechaFin = null;
        String observaciones = null;
        Double pagoInicial = null;

        try {
            clienteId = Long.parseLong(data[0]);
        } catch (Exception e) {
            return Resultado.error("Error: clienteId inválido");
        }

        if (data.length > 1 && data[1] != null && !data[1].isBlank()) {
            try { barberoId = Long.parseLong(data[1]); } catch (Exception e) { return Resultado.error("Error: barberoId inválido"); }
        }

        serviciosCsv = data[2] == null || data[2].isBlank() ? "" : data[2];

        fechaInicio = data[3];
        if (fechaInicio == null || fechaInicio.isBlank()) return Resultado.error("Error: fechaHoraInicio inválida o vacía");

        if (data.length > 4 && data[4] != null && !data[4].isBlank()) fechaFin = data[4];
        if (data.length > 5 && data[5] != null && !data[5].isBlank()) observaciones = data[5];
        if (data.length > 6 && data[6] != null && !data[6].isBlank()) {
            try { pagoInicial = Double.parseDouble(data[6]); } catch (Exception e) { return Resultado.error("Error: pagoInicial inválido"); }
        }

        CreateCitaDTO dto = new CreateCitaDTO(clienteId, barberoId, serviciosCsv, fechaInicio, fechaFin, observaciones, pagoInicial);
        return Resultado.ok(dto);
    }

    public String toStringCorreo() {
        return "Cita {\r\n" +
                "  clienteId='" + clienteId + "'\r\n" +
                "  barberoId='" + (barberoId == null ? "" : barberoId) + "'\r\n" +
                "  servicios='" + serviciosCsv + "'\r\n" +
                "  fechaInicio='" + fechaHoraInicio + "'\r\n" +
                "  fechaFin='" + (fechaHoraFin == null ? "" : fechaHoraFin) + "'\r\n" +
                "  observaciones='" + (observaciones == null ? "" : observaciones) + "'\r\n" +
                "  pagoInicial='" + (pagoInicial == null ? "" : pagoInicial) + "'\r\n" +
                "}";
    }
}
