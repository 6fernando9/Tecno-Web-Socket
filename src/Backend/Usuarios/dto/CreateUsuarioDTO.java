package Backend.Usuarios.dto;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class CreateUsuarioDTO {
    public String nombre;
    public String apellido;
    public String email;
    public String telefono;
    public String password;
    public String rol;
    public String estado;
    public String deletedAt;

    public CreateUsuarioDTO(){}
    public CreateUsuarioDTO(String nombre, String apellido, String email, String telefono, String password, String rol,String estado,String deletedAt){
          this.nombre = nombre;
          this.apellido = apellido;
          this.email = email;
          this.telefono = telefono;
          this.password = password;
          this.rol = rol;
          this.estado = estado;
          this.deletedAt = deletedAt;
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

        if (GeneralMethods.esCampoNuloVacio(nombre)) {
            return Resultado.error("Error: el campo 'nombre' no puede ser nulo o vacio");
        }
        if (GeneralMethods.esCampoNuloVacio(apellido)) {
            return Resultado.error("Error: el campo 'apellido' no puede ser nulo o vacio");
        }
        if (GeneralMethods.esCampoNuloVacio(email)) {
            return Resultado.error("Error: el campo 'email' no puede ser nulo o vacio");
        }
        if (GeneralMethods.esCampoNuloVacio(telefono)) {
            return Resultado.error("Error: el campo 'telefono' no puede ser nulo o vacio");
        }
        if (GeneralMethods.esCampoNuloVacio(password)) {
            return Resultado.error("Error: el campo 'password' no puede ser nulo o vacio");
        }
        if (GeneralMethods.esCampoNuloVacio(rol)) {
            return Resultado.error("Error: el campo 'rol' no puede ser nulo o vacio");
        }

        if(!GeneralUsuarioSQLUtils.esRolPermitido(rol)){
            return Resultado.error("Error...el valor de rol es un valor diferente de lo esperado...");
        }
        if (password.length() < 6) {
            return Resultado.error("Error: la contraseña debe tener al menos 6 caracteres");
        }

        CreateUsuarioDTO usuario = new CreateUsuarioDTO(nombre, apellido, email, telefono, password, rol,null,null);
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
<html>\r
  <body style="font-family: Arial, sans-serif; padding: 10px;">\r
    <h2 style="color:#4CAF50;">✅ Usuario creado exitosamente</h2>\r
    <p><b>Nombre:</b> %s %s</p>\r
    <p><b>Email:</b> %s</p>\r
    <p><b>Teléfono:</b> %s</p>\r
    <p><b>Contraseña:</b> %s</p>\r
    <p><b>Rol:</b> %s</p>\r
    <br>\r
    <p>Bienvenido al sistema 🎉</p>\r
  </body>\r
</html>\r
""".formatted(nombre, apellido, email, telefono, password, rol);
        return html;
    }


}
