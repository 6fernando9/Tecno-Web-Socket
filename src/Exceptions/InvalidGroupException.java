package Exceptions;

public class InvalidGroupException extends IllegalArgumentException{
    public InvalidGroupException(){
        super("Error Grupo Invalido..");
    }
    public InvalidGroupException(String args) {
        super(args);
    }
}
