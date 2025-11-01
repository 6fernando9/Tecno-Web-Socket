package Exceptions;

public class InvalidDataException extends IllegalArgumentException {
    public InvalidDataException(String message) {
        super(message);
    }
    public InvalidDataException(){
        super("Error...datos no validos");
    }
}
