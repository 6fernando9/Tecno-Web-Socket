package Backend.Usuarios.dto;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class CreateUsuarioDTO {
    public String nombre;
    public String apellido;
    public String email;
    public String telefono;
    public String password;
    public String rol;

    public CreateUsuarioDTO(String nombre, String apellido, String email, String telefono, String password, String rol){
          this.nombre = nombre;
          this.apellido = apellido;
          this.email = email;
          this.telefono = telefono;
          this.password = password;
          this.rol = rol;
    }
    public static Resultado<CreateUsuarioDTO> crearUsuarioMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        // Verificamos cantidad mínima de campos
        if (data.length < 6) {
            return Resultado.error("Error: se esperaba que el usuario introduzca al menos 6 campos (nombre, apellido, email, telefono, password, rol)");
        }
        //"INSERT INTO usuarios (nombre, apellido, email, telefono, password, rol) VALUES (?, ?, ?, ?, ?, ?)";
        //createuser["evans","balcazar veizaga","evans@gmail.com","76773834","12345678","barbero"]
        String nombre = data[0];
        String apellido = data[1];
        String email = data[2];
        String telefono = data[3];
        String password = data[4];
        String rol = data[5];

        if (nombre == null || nombre.equalsIgnoreCase("null")) {
            return Resultado.error("Error: el campo 'nombre' no puede ser nulo");
        }
        if (apellido == null || apellido.equalsIgnoreCase("null")) {
            return Resultado.error("Error: el campo 'apellido' no puede ser nulo");
        }
        if (email == null || email.equalsIgnoreCase("null")) {
            return Resultado.error("Error: el campo 'email' no puede ser nulo");
        }
        if (telefono == null || telefono.equalsIgnoreCase("null")) {
            return Resultado.error("Error: el campo 'telefono' no puede ser nulo");
        }
        if (password == null || password.equalsIgnoreCase("null")) {
            return Resultado.error("Error: el campo 'password' no puede ser nulo");
        }
        if (rol == null || rol.equalsIgnoreCase("null")) {
            return Resultado.error("Error: el campo 'rol' no puede ser nulo");
        }
        System.out.println("rol: "+ rol);
        if(!GeneralUsuarioSQLUtils.esRolPermitido(rol)){
            return Resultado.error("Error...el valor de rol es un valor diferente de lo esperado...");
        }
        if (password.length() < 6) {
            return Resultado.error("Error: la contraseña debe tener al menos 6 caracteres");
        }

        CreateUsuarioDTO usuario = new CreateUsuarioDTO(nombre, apellido, email, telefono, password, rol);
        return Resultado.ok(usuario);
    }


    @Override
    public String toString() {
        return "Usuario creado {" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", password='" + password + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }

    public String toStringCorreo() {
        return "Usuario creado {\r\n" +
                "  nombre = '" + nombre + "'\r\n" +
                "  apellido = '" + apellido + "'\r\n" +
                "  email = '" + email + "'\r\n" +
                "  telefono = '" + telefono + "'\r\n" +
                "  password = '" + password + "'\r\n" +
                "  rol = '" + rol + "'\r\n" +
                "}";
    }

    public String toStringCorreoHTML() {
        String html = """
    <div style="font-family: Arial, sans-serif; padding: 10px;">
      <h2 style="color:#4CAF50;">✅ Usuario creado exitosamente</h2>
      <p><b>Nombre:</b> %s %s</p>
      <p><b>Email:</b> %s</p>
      <p><b>Teléfono:</b> %s</p>
      <p><b>Contraseña:</b> %s</p>
      <p><b>Rol:</b> %s</p>
      <br>
      <p>Bienvenido al sistema 🎉</p>
    </div>
    """.formatted(nombre, apellido, email, telefono, password, rol);

        // Normaliza los saltos a CRLF para SMTP
        return html.replace("\r\n", "\n").replace("\n", "\r\n");
    }


}
