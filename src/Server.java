import java.io.*;
import java.net.*;


public class Server {
    // puerto de abertura del servidor
    static final int PORT = 5000;
    public Server() {
        try{
            // intenta abrir este puerto el servidor
            ServerSocket socketServer = new ServerSocket(PORT);
            System.out.println(" S: listening on port: " + PORT);
            for (int i = 0; i < 3; i++) {
                Socket socketClient = socketServer.accept(); // Create Object - or process
                System.out.println("S: Sirvo al cliente: " + i);
                //send information to client
                DataOutputStream output = new DataOutputStream(socketClient.getOutputStream());
                output.writeBytes("Hola Cliente " + i + "IP:" + socketClient.getRemoteSocketAddress());
                socketClient.close();
            }
            System.out.println("S: it's too many clients for now");
        }catch(Exception e){
            System.out.println("Throw - " + e.getMessage());
        }
    }
    public static void main(String[] args){
        Server server = new Server();
    }
}
