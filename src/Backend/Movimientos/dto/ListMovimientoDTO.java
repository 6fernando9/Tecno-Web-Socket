package Backend.Movimientos.dto;

import Backend.Movimientos.GeneralMovimientoUtils;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Utils.TecnoUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ListMovimientoDTO {

    public Long productoId;
    public String tipo;
    public LocalDateTime desde;
    public LocalDateTime hasta;
    public String motivo;

    public ListMovimientoDTO() {}

    public static Resultado<ListMovimientoDTO> crearMedianteSubject(String subject) {
        String[] data = TecnoUtils.procesarString(subject);

        if (data.length < 5) {
            return Resultado.error("Error: Se esperaban 5 atributos (productoId, desde, hasta, tipo, motivo). Use 'null' para omitir filtros.");
        }

        ListMovimientoDTO dto = new ListMovimientoDTO();
        DateTimeFormatter soloFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter fechaHora = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        try {

            String productoIdTemp = obtenerNuloOValor(data[0]);
            if (productoIdTemp != null) {
                dto.productoId = Long.parseLong(productoIdTemp);
            }

            String desdeTemp = obtenerNuloOValor(data[1]);
            if (desdeTemp != null) {
                dto.desde = desdeTemp.contains(" ")
                        ? LocalDateTime.parse(desdeTemp, fechaHora)
                        : LocalDate.parse(desdeTemp, soloFecha).atTime(LocalTime.MIN);
            }

            String hastaTemp = obtenerNuloOValor(data[2]);
            if (hastaTemp != null) {
                dto.hasta = hastaTemp.contains(" ")
                        ? LocalDateTime.parse(hastaTemp, fechaHora)
                        : LocalDate.parse(hastaTemp, soloFecha).atTime(LocalTime.MAX);
            }

            String tipoTemp = obtenerNuloOValor(data[3]);
            if (tipoTemp != null) {
                String tipoLimpiado = tipoTemp.toLowerCase();
                dto.tipo = tipoLimpiado;
            }

            dto.motivo = obtenerNuloOValor(data[4]);

            return Resultado.ok(dto);

        } catch (NumberFormatException e) {
            return Resultado.error("Error: El productoId debe ser un número válido.");
        } catch (DateTimeParseException e) {
            return Resultado.error("Error: Formato de fecha inválido. Use dd-MM-yyyy o dd-MM-yyyy HH:mm.");
        } catch (Exception e) {
            return Resultado.error("Error al procesar filtros: " + e.getMessage());
        }
    }
    private static String obtenerNuloOValor(String valor){
        if(valor.trim().length() == 0 ){
            return null;
        }
        return valor.trim().equalsIgnoreCase("null") ? null: valor.trim();
    }

    @Override
    public String toString() {
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return "ListMovimientoDTO{" +
                "productoId=" + (productoId ) +
                ", tipo='" + (tipo ) + '\'' +
                ", desde=" + (desde) +
                ", hasta=" + (hasta ) +
                ", motivo='" + (motivo ) + '\'' +
                '}';
    }
}