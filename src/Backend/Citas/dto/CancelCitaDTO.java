package Backend.Citas.dto;

import Backend.Utils.GeneralMethods.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class CancelCitaDTO {
    public long citaId;
    public Long usuarioId;
    public String motivo; // optional

    public CancelCitaDTO() {}

    public CancelCitaDTO(long citaId, Long usuarioId, String motivo) {
        this.citaId = citaId;
        this.usuarioId = usuarioId;
        this.motivo = motivo;
    }

    // Subject expected: cita_cancel["citaId","usuarioId","motivo"]
    public static Resultado<CancelCitaDTO> crearMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 2) return Resultado.error("Error: se esperan al menos citaId y usuarioId");

        long citaId;
        try { citaId = Long.parseLong(data[0]); } catch (Exception e) { return Resultado.error("Error: citaId inválido"); }

        Long usuarioId = null;
        try { usuarioId = Long.parseLong(data[1]); } catch (Exception e) { return Resultado.error("Error: usuarioId inválido"); }

        String motivo = null;
        if (data.length > 2 && data[2] != null && !data[2].isBlank()) motivo = data[2];

        CancelCitaDTO dto = new CancelCitaDTO(citaId, usuarioId, motivo);
        return Resultado.ok(dto);
    }
}
