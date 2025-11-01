package Backend.Usuarios.dto;

import Backend.Usuarios.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class UpdateUsuarioDTO extends CreateUsuarioDTO{
    public Long id;
    public UpdateUsuarioDTO(){
        super();
    }
    public UpdateUsuarioDTO(Long id, String nombre, String apellido, String email, String telefono, String password, String rol) {
        super(nombre, apellido, email, telefono, password, rol);
        this.id = id;
    }

    public static Resultado<UpdateUsuarioDTO> crearUpdateUsuarioMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 7) {
            return Resultado.error("Error: se esperaban al menos 7 campos (id, nombre, apellido, email, telefono, password, rol)");
        }
        String id = data[0];
        String subjectParaReutilizarCreate = String.format(
                "[\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"]",
                data[1], data[2], data[3], data[4], data[5], data[6]
        );
        Resultado<CreateUsuarioDTO> resultadoCreateDTO = CreateUsuarioDTO.crearUsuarioMedianteSubject(subjectParaReutilizarCreate);
        if(!resultadoCreateDTO.esExitoso()){
            return Resultado.error(resultadoCreateDTO.getError());
        }
        if(id == null || id.equalsIgnoreCase("null")){
            return Resultado.error("Error..el campo id no puede ser nulo");
        }
        CreateUsuarioDTO createUsuarioDTO = resultadoCreateDTO.getValor();
        Long idDto;
        try {
            idDto = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return Resultado.error("Error: el campo 'id' debe ser numérico");
        }
        UpdateUsuarioDTO updateUsuarioDTO =  new UpdateUsuarioDTO(idDto, createUsuarioDTO.nombre, createUsuarioDTO.apellido, createUsuarioDTO.email, createUsuarioDTO.telefono, createUsuarioDTO.password, createUsuarioDTO.rol);
        return Resultado.ok(updateUsuarioDTO);
    }

    @Override
    public String toString() {
        return "Usuario actualizado {" +
                "id=" + id +
                ", nombre='" + super.nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", password='" + password + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }

    public String toStringCorreo() {
        return "Usuario actualizado {\r\n" +
                "  id = " + id + "\r\n" +
                "  nombre = '" + nombre + "'\r\n" +
                "  apellido = '" + apellido + "'\r\n" +
                "  email = '" + email + "'\r\n" +
                "  telefono = '" + telefono + "'\r\n" +
                "  password = '" + password + "'\r\n" +
                "  rol = '" + rol + "'\r\n" +
                "}";
    }
}
