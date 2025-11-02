package Backend.Utils.dto;
import Backend.Usuarios.Resultado;
import Utils.TecnoUtils;
//clase para usar solamente para extraer el campo string , ejemplo accion["cadena"]
public class IdentificadorStrDTO {
    public String message;
    public IdentificadorStrDTO(String message){
        this.message = message;
    }
    public static Resultado<IdentificadorStrDTO> createMensajePatronDTO(String subject)  {
        String[] data = TecnoUtils.procesarString(subject);
        if(data.length < 1){
            return Resultado.error("Error..se esperaba que el usuario brindara al menos un campo()");
        }
        String dataNull = null;
        String message = data[0];
        if(message.equals("null")){
            //podriamos asignar el valor de null en el caso de que si lo fuese
            return Resultado.error("Error data id no puede ser null");
        }
        String messageDto = message;
        return Resultado.ok(new IdentificadorStrDTO(messageDto));
    }
    public String toStringCorreo(){
        return "Message {" + "\r\n" +
                "message=" + message +'\'' + "\r\n" +
                '}';
    }
}
