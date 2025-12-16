package Backend.Citas.dto;

import Backend.Servicio.dto.UpdateServicioDTO;

import java.util.HashSet;
import java.util.Set;

public class CitaDTO {
    public Long id;
    public String fecha;
    public String estado;
    public Double pagoInicial;
    public Double montoTotal;
    public Double porcentajeCita;
    public Long barberoId;
    public Long clienteId;
    public Set<UpdateServicioDTO> servicios = new HashSet<>();
    public CitaDTO(){}
    public CitaDTO(Long id, String fecha, String estado, double pagoInicial, double montoTotal, double porcentajeCita, Long barberoId, Long clienteId, Set<UpdateServicioDTO> servicios) {
        this.id = id;
        this.fecha = fecha;
        this.estado = estado;
        this.pagoInicial = pagoInicial;
        this.montoTotal = montoTotal;
        this.porcentajeCita = porcentajeCita;
        this.barberoId = barberoId;
        this.clienteId = clienteId;
        this.servicios = servicios;
        if (servicios != null) {
            this.servicios = servicios;
        }
    }
}
