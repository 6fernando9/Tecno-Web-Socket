package Backend.Movimientos.dto;

import Backend.Usuarios.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class UpdateMovimientoDTO {
    public long id;
    public Integer cantidad; // nullable
    public String motivo; // nullable
    public String fecha; // nullable, expected format YYYY-MM-DD

    public UpdateMovimientoDTO() {}

    public UpdateMovimientoDTO(long id, Integer cantidad, String motivo, String fecha) {
        this.id = id;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fecha = fecha;
    }

    public static Resultado<UpdateMovimientoDTO> crearMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 1) {
            return Resultado.error("Error: se esperaba al menos el id del movimiento");
        }
        long id;
        try {
            id = Long.parseLong(data[0]);
        } catch (Exception e) {
            return Resultado.error("Error: id inválido");
        }

        Integer cantidad = null;
        String motivo = null;
        String fecha = null;

        if (data.length > 1 && !data[1].isBlank()) {
            try {
                cantidad = Integer.parseInt(data[1]);
            } catch (Exception e) {
                return Resultado.error("Error: cantidad inválida");
            }
            if (cantidad <= 0) return Resultado.error("Error: la cantidad debe ser mayor a 0");
        }
        if (data.length > 2) motivo = data[2];
        if (data.length > 3) fecha = data[3];

        UpdateMovimientoDTO dto = new UpdateMovimientoDTO(id, cantidad, motivo, fecha);
        return Resultado.ok(dto);
    }
}
