package Backend.Usuarios.dto;

import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class UsuarioDTO {
    public Long id;
    public String username;
    public String nombre;
    public String password;
    public String correo;
    public Long telefono;
    public String tipo;

    public UsuarioDTO(Long id,String username,String nombre, String password,String correo, Long telefono,String tipo){
          this.id = id;
          this.username = username;
          this.nombre = nombre;
          this.password = password;
          this.correo = correo;
          this.telefono = telefono;
          this.tipo = tipo;
    }
    public static UsuarioDTO crearUsuarioMedianteSubject(String subject) throws InvalidDataException{
        String[] data = TecnoUtils.procesarString(subject);
        if(data.length < 7){
            throw new InvalidDataException("Error..se esperaba que el usuario introduzca al menos 7 campos");
        }
        String dataNull = null;
        String id = data[0];
        String username = data[1];
        String nombre = data[2];
        String password = data[3];
        String correo = data[4];
        String telefono = data[5];
        String tipo = data[6];
        if(id.equals("null")){
            //podriamos asignar el valor de null en el caso de que si lo fuese
            throw new InvalidDataException("Error data id no puede ser null");
        }
        if(username.equals("null")){
            throw new InvalidDataException("Error data username no puede ser null");
        }
        if(nombre.equals("null")){
            throw new InvalidDataException("Error data nombre no puede ser null");
        }
        if(password.equals("null")){
            throw new InvalidDataException("Error data password no puede ser null");
        }
        if(correo.equals("null")){
            throw new InvalidDataException("Error data correo no puede ser null");
        }
        if(telefono.equals("null")){
            throw new InvalidDataException("Error data telefono no puede ser null");
        }
        if(tipo.equals("null")){
            throw new InvalidDataException("Error data tipo no puede ser null");
        }
        Long idDto = Long.parseLong(id);
        //Test para Nulos
        //String usernameDto = username.equals("null") ? null : username;
        String usernameDto = username;
        String nombreDto = nombre;
        String passwordDto = password;
        String correoDto = correo;
        Long telefonoDto = Long.parseLong(telefono);
        String tipoDto = tipo;
        return new UsuarioDTO(idDto,usernameDto,nombreDto,passwordDto,correoDto,telefonoDto,tipoDto);
    }

    @Override
    public String toString() {
        return "Usuario creado {" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nombre='" + nombre + '\'' +
                ", password='" + password + '\'' +
                ", correo='" + correo + '\'' +
                ", telefono=" + telefono +
                ", tipo='" + tipo + '\'' +
                '}';
    }
    public String toStringCorreo(){
        return "Usuario creado {" + "\r\n" + // Inicia con un salto de línea si quieres
                "id=" + id +'\'' + "\r\n" +
                "username='" + username + '\'' + "\r\n" +
                "nombre='" + nombre + '\'' + "\r\n" +
                "password='" + password + '\'' + "\r\n" +
                "correo='" + correo + '\'' + "\r\n" +
                "telefono=" + telefono + "\r\n" +
                "tipo='" + tipo + '\'' + "\r\n" +
                '}';
    }
}
