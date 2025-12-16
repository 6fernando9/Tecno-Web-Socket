package Backend.Citas.dto;

import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Utils.TecnoUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateCitaV2DTO {
    public Long clienteId;
    public Long barberoId;
    public Set<Long> serviciosIds;
    public String tipoPago;
    public double pagoInicial;
    public LocalDateTime fecha;
    public CreateCitaV2DTO(){}
    public CreateCitaV2DTO(Long clienteId,Long barberoId,Set<Long> serviciosIds,String tipoPago,double pagoInicial,LocalDateTime fecha){
        this.clienteId = clienteId;
        this.barberoId = barberoId;
        this.serviciosIds = serviciosIds;
        this.tipoPago = tipoPago;
        this.pagoInicial = pagoInicial;
        this.fecha = fecha;
    }
    public static Resultado<CreateCitaV2DTO> createCitaV2DTOResultado(String subject) {
        String[] data = TecnoUtils.procesarStringSeguro(subject);
        if (data.length < 6) {
            return Resultado.error("Error: se esperaban al menos 6 campos (clienteId, barberoId, tipoPago, pagoInicial, fecha, serviciosIds)");
        }

        String clienteIdEnt = data[0];
        String barberoIdEnt = data[1];
        String tipoPagoEnt = data[2];
        String pagoInicEnt = data[3];
        String fechaEnt = data[4];
        String serviciosEnt = data[5];
        if (GeneralMethods.esCampoNuloVacio(clienteIdEnt) ||
                GeneralMethods.esCampoNuloVacio(barberoIdEnt) ||
                GeneralMethods.esCampoNuloVacio(fechaEnt) ||
                GeneralMethods.esCampoNuloVacio(serviciosEnt)) {
            return Resultado.error("Error: Los campos obligatorios no pueden estar vacíos.");
        }

        try {

            Long clienteId = Long.parseLong(clienteIdEnt.trim());
            Long barberoId = Long.parseLong(barberoIdEnt.trim());
            double pagoInicial = Double.parseDouble(pagoInicEnt.trim());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime fechaHora = LocalDateTime.parse(fechaEnt.trim(), formatter);

            String[] serviciosSplit = serviciosEnt.split(",");
            Set<Long> serviciosSet = Arrays.stream(serviciosSplit)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());

            if (serviciosSet.isEmpty()) {
                return Resultado.error("Error: Debe seleccionar al menos un servicio válido.");
            }

            CreateCitaV2DTO dto = new CreateCitaV2DTO();
            dto.clienteId = clienteId;
            dto.barberoId = barberoId;
            dto.tipoPago = tipoPagoEnt.trim();
            dto.pagoInicial = pagoInicial;
            dto.fecha = fechaHora;
            dto.serviciosIds = serviciosSet;
            return Resultado.ok(dto);
        } catch (NumberFormatException e) {
            return Resultado.error("Error: Uno de los campos numéricos (ID o Monto) tiene un formato inválido.");
        } catch (DateTimeParseException e) {
            return Resultado.error("Error: El formato de fecha debe ser dd-MM-yyyy HH:mm (Ej: 16-12-2025 15:30)");
        } catch (Exception e) {
            return Resultado.error("Error al procesar los datos: " + e.getMessage());
        }
    }
}
