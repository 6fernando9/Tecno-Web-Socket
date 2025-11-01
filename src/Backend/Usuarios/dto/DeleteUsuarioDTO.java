package Backend.Usuarios.dto;

import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class DeleteUsuarioDTO {
    public Long id;
    public DeleteUsuarioDTO(Long id){
        this.id = id;
    }
    public static DeleteUsuarioDTO createDeleteUsuarioDTO(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        if(data.length < 1){
            throw new InvalidDataException("Error..se esperaba que el usuario introduzca al menos 7 campos");
        }
        String dataNull = null;
        String id = data[0];
        if(id.equals("null")){
            //podriamos asignar el valor de null en el caso de que si lo fuese
            throw new InvalidDataException("Error data id no puede ser null");
        }
        Long idDto = Long.parseLong(id);
        return new DeleteUsuarioDTO(idDto);
    }
    public String toStringCorreo(){
        return "Usuario eliminado {" + "\r\n" +
                "id=" + id +'\'' + "\r\n" +
                '}';
    }
}
