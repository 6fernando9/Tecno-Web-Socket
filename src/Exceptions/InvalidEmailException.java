package Exceptions;

public class InvalidEmailException extends IllegalArgumentException {
    public InvalidEmailException(){
        super("Error.. Correo no valido");
    }
    public InvalidEmailException(String message) {
        super(message);
    }
}
