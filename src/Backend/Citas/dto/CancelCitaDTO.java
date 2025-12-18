package Backend.Citas.dto;

import Backend.Utils.GeneralMethods.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class CancelCitaDTO {
    public long citaId;
    public Long clienteId;

    public CancelCitaDTO() {}

    public CancelCitaDTO(long citaId, Long usuarioId) {
        this.citaId = citaId;
        this.clienteId = usuarioId;
    }

    // Subject expected: cita_cancel["citaId","usuarioId","motivo"]
    public static Resultado<CancelCitaDTO> crearMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 2) return Resultado.error("Error: se esperan al menos citaId y usuarioId");

        long citaId;
        try { citaId = Long.parseLong(data[0]); } catch (Exception e) { return Resultado.error("Error: citaId inválido"); }

        Long usuarioId = null;
        try { usuarioId = Long.parseLong(data[1]); } catch (Exception e) { return Resultado.error("Error: usuarioId inválido"); }


        CancelCitaDTO dto = new CancelCitaDTO(citaId, usuarioId);
        return Resultado.ok(dto);
    }
}
