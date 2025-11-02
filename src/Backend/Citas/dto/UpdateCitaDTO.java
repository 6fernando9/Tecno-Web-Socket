package Backend.Citas.dto;

import Backend.Utils.GeneralMethods.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class UpdateCitaDTO {
    public long citaId;
    public Long usuarioId; // id del cliente que solicita la actualización
    public Long barberoId; // nuevo barbero (opcional)
    public String fechaHoraInicio; // ISO: YYYY-MM-DDTHH:MM (opcional)
    public String fechaHoraFin; // ISO (opcional)

    public UpdateCitaDTO() {}

    public UpdateCitaDTO(long citaId, Long usuarioId, Long barberoId, String fechaHoraInicio, String fechaHoraFin) {
        this.citaId = citaId;
        this.usuarioId = usuarioId;
        this.barberoId = barberoId;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
    }

    // Subject expected: cita_update["citaId","usuarioId","barberoId","2025-11-05T10:00","2025-11-05T10:30"]
    // Use empty string "" for optional fields
    public static Resultado<UpdateCitaDTO> crearMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 2) return Resultado.error("Error: se esperan al menos citaId y usuarioId");

        long citaId;
        try { citaId = Long.parseLong(data[0]); } catch (Exception e) { return Resultado.error("Error: citaId inválido"); }

        Long usuarioId = null;
        try { usuarioId = Long.parseLong(data[1]); } catch (Exception e) { return Resultado.error("Error: usuarioId inválido"); }

        Long barberoId = null;
        if (data.length > 2 && data[2] != null && !data[2].isBlank()) {
            try { barberoId = Long.parseLong(data[2]); } catch (Exception e) { return Resultado.error("Error: barberoId inválido"); }
        }

        String inicio = null;
        String fin = null;
        if (data.length > 3 && data[3] != null && !data[3].isBlank()) inicio = data[3];
        if (data.length > 4 && data[4] != null && !data[4].isBlank()) fin = data[4];

        UpdateCitaDTO dto = new UpdateCitaDTO(citaId, usuarioId, barberoId, inicio, fin);
        return Resultado.ok(dto);
    }
}
