package Backend.Usuarios.dto;

import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class MensajeUsuarioDTO {
    public String message;
    public MensajeUsuarioDTO(String message){
        this.message = message;
    }
    public static MensajeUsuarioDTO createMensajePatronDTO(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        if(data.length < 1){
            throw new InvalidDataException("Error..se esperaba que el usuario introduzca al menos 7 campos");
        }
        String dataNull = null;
        String message = data[0];
        if(message.equals("null")){
            //podriamos asignar el valor de null en el caso de que si lo fuese
            throw new InvalidDataException("Error data id no puede ser null");
        }
        String messageDto = message;
        return new MensajeUsuarioDTO(messageDto);
    }
    public String toStringCorreo(){
        return "Message {" + "\r\n" +
                "message=" + message +'\'' + "\r\n" +
                '}';
    }
}
