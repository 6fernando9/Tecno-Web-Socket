package Backend.Usuarios.dto;

public class UsuarioDTO {
    public Long id;
    public String nombre;
    public String password;
    public String correo;
    public Long telefono;
    public String tipo;

    public UsuarioDTO(Long id,String nombre, String password,String correo, Long telefono,String tipo){
          this.id = id;
          this.nombre = nombre;
          this.password = password;
          this.correo = correo;
          this.telefono = telefono;
          this.tipo = tipo;
    }
}
