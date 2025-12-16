package Backend.Usuarios.dto;

import Backend.Servicio.dto.UpdateServicioDTO;

import java.util.HashSet;
import java.util.Set;

public class BarberoServicioDTO {
    public Long id;
    public String nombre;
    public String apellido;
    public String email;
    public String telefono;
    public String estado;
    public Set<UpdateServicioDTO> servicios = new HashSet<>();
    public BarberoServicioDTO(){}
    public BarberoServicioDTO(Long id, String nombre, String apellido, String email, String telefono,String estado, Set<UpdateServicioDTO> servicios) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.estado = estado;
        if (servicios != null) {
            this.servicios = servicios;
        }
    }
}
