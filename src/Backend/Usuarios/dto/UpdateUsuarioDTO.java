package Backend.Usuarios.dto;

public class UpdateUsuarioDTO {
    public String username;
    public String nombre;
    public String password;
    public String correo;
    public Long telefono;
    public String tipo;

    public UpdateUsuarioDTO(String username,String nombre, String password,String correo, Long telefono,String tipo){
        this.username = username;
        this.nombre = nombre;
        this.password = password;
        this.correo = correo;
        this.telefono = telefono;
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Usuario creado {" +
                "username='" + username + '\'' +
                ", nombre='" + nombre + '\'' +
                ", password='" + password + '\'' +
                ", correo='" + correo + '\'' +
                ", telefono=" + telefono +
                ", tipo='" + tipo + '\'' +
                '}';
    }
    public String toStringCorreo(){
        return "Usuario creado {" + "\r\n" +
                "username='" + username + '\'' + "\r\n" +
                "nombre='" + nombre + '\'' + "\r\n" +
                "password='" + password + '\'' + "\r\n" +
                "correo='" + correo + '\'' + "\r\n" +
                "telefono=" + telefono + "\r\n" +
                "tipo='" + tipo + '\'' + "\r\n" +
                '}';
    }
}
