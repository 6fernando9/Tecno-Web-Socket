package Backend.Horarios.dto;

import Backend.Usuarios.dto.UpdateUsuarioDTO;

import java.util.ArrayList;
import java.util.List;

public class UsuarioHorarioDTO extends UpdateUsuarioDTO {
    List<HorarioDTO> horarios;
    public UsuarioHorarioDTO(){}
    public UsuarioHorarioDTO(Long id, String nombre, String apellido, String email, String telefono, String password, String rol,String estado) {
          super(id,nombre,apellido,email,telefono,password,rol,estado);
          this.horarios = new ArrayList<>();
    }

    public List<HorarioDTO> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioDTO> horarios) {
        this.horarios = horarios;
    }
}
