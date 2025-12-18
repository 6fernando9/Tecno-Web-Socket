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

    @Override
    public String toString() {
        return "BarberoServicioDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", servicios=" + servicios +
                ", estado='" + estado + '\'' +
                '}';
    }
    public String imprimirDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== DETALLE DEL BARBERO ==========\r\n");
        sb.append("ID       : ").append(id).append("\r\n");
        sb.append("Barbero  : ").append(nombre).append(" ").append(apellido).append("\r\n");
        sb.append("Contacto : ").append(telefono).append(" | ").append(email).append("\r\n");
        sb.append("Estado   : ").append(estado.toUpperCase()).append("\r\n");

        sb.append("---------- SERVICIOS ASIGNADOS ----------\r\n");
        if (servicios == null || servicios.isEmpty()) {
            sb.append("No tiene servicios registrados.\r\n");
        } else {
            for (UpdateServicioDTO s : servicios) {
                sb.append(String.format(" - [%d] %-15s | Precio: %.2f | Duración: %d min\r\n",
                        s.id,
                        s.nombre,
                        s.precio,
                        s.duracion));
            }
        }
        sb.append("=========================================");
        return sb.toString();
    }
}
