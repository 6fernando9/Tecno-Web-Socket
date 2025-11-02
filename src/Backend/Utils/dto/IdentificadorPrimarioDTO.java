package Backend.Utils.dto;

import Backend.Usuarios.Resultado;
import Utils.TecnoUtils;
//clase para usarla solamente para extraer un campo ID de tipo Long, ejemplo Accion["2"]
public class IdentificadorPrimarioDTO {
    public Long id;
    public IdentificadorPrimarioDTO(Long id){
        this.id = id;
    }
    public static Resultado<IdentificadorPrimarioDTO> createDeleteUsuarioDTO(String subject){
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
        return Resultado.ok(new IdentificadorPrimarioDTO(idDto));
    }
    public String toStringCorreo(){
        return "dtoId {" + "\r\n" +
                "id=" + id +'\'' + "\r\n" +
                '}';
    }
}
