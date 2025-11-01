package Backend.Usuarios.dto;

import Backend.Usuarios.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class DeleteUsuarioDTO {
    public Long id;
    public DeleteUsuarioDTO(Long id){
        this.id = id;
    }
    public static Resultado<DeleteUsuarioDTO> createDeleteUsuarioDTO(String subject){
        String[] data = TecnoUtils.procesarString(subject);
        if(data.length < 1){
            return Resultado.error("Error..se esperaba que el usuario introduzca al menos 1 campo");
        }
        String dataNull = null;
        String id = data[0];
        if(id.equals("null")){
            //podriamos asignar el valor de null en el caso de que si lo fuese
            return Resultado.error("Error data id no puede ser null");
        }
        Long idDto = Long.parseLong(id);
        return Resultado.ok(new DeleteUsuarioDTO(idDto));
    }
    public String toStringCorreo(){
        return "Usuario eliminado {" + "\r\n" +
                "id=" + id +'\'' + "\r\n" +
                '}';
    }
}
