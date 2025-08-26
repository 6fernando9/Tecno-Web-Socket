import java.io.*;
import java.net.*;

public class Client {
    // puede ser IP o Dominio - del server
    static final String HOST = "localhost";
    // puerto donde el servidor esta escuchando
    static final int PORT = 5000;
    public Client(){
        try{
            Socket socketClient = new Socket(HOST,PORT);
            // entrada para leer desde el socket
            BufferedReader input = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            //DataOutputStream output = new DataOutputStream(socketClient.getOutputStream());
            System.out.println("C: Connecting to <" + HOST + ">");
            // imprime la respuesta del servidor
            System.out.println("S: "+ input.readLine());
            socketClient.close();
            System.out.println("C: Disconnect of <" + HOST + ">" );
        }catch(Exception e){
            System.out.println("C - Throw: "+ e.getMessage());
        }
    }
    public static void main(String[] args){
        Client client = new Client();
    }
}
