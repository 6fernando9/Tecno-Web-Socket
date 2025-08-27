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
                // para recibir datos del cliente
                BufferedReader input = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                // envio saludo del servidor al cliente
                output.writeBytes("Hola Cliente " + i + "IP:" + socketClient.getRemoteSocketAddress() + "\n");
                //servidor lee el mensaje del cliente
                String clientMsg = input.readLine();
                System.out.println(" el cliente dice: " + clientMsg);
                // Servidor responde al cliente -envia mensaje de nuveo
                output.writeBytes("Recibí tu mensaje: " + clientMsg + "\n");
                socketClient.close();
            }
            System.out.println("S: it's too many clients for now");
            socketServer.close();
        }catch(Exception e){
            System.out.println("Throw - " + e.getMessage());
        }
    }
    public static void main(String[] args){
        Server server = new Server();
    }
}
