package Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketUtils {
    public static int SMTP_PORT = 25;
    public static int POP3_PORT = 110;
    public static String MAIL_SERVER = "mail.tecnoweb.org.bo";


    public static boolean esEntradaValida(Socket socket, BufferedReader input, DataOutputStream output){
        return socket != null && input != null && output != null;
    }
    public static void closeServices(Socket socket, BufferedReader input, DataOutputStream output) throws IOException {
        socket.close();
        input.close();
        output.close();
    }
    public static String getMultiline(BufferedReader input) throws IOException {
        String lines = "";
        while (true) {
            String line = input.readLine();
            if (line == null){
                throw new IOException("Socket.Socket.Server unawares closed the connection");
            }
            // siempre la respuesta del servidor que generalemente es largo, termina en punto
            if(line.equals(".")) {
                break;
            }
            if ((!line.isEmpty()) && (line.charAt(0) == '.')) {
                line = line.substring(1);
            }
            lines = lines + "\n" + line;
        }
        return lines;
    }
    public static List<String> getIdsForListCommand(BufferedReader input) throws IOException{
        List<String> listaDeIds = new ArrayList<>();
        int contador = 0;
        String lines = "";
        while(true){
            String line = input.readLine();
            contador ++ ;
            if (line.length() > 1 && contador > 1) {
                String id = line.substring(0,line.indexOf(" "));
                listaDeIds.add(id);
            }
            if (line == null){
                throw new IOException("Socket.Socket.Server unawares closed the connection");
            }
            // siempre la respuesta del servidor que generalemente es largo, termina en punto
            if(line.equals(".")) {
                break;
            }
            if ((!line.isEmpty()) && (line.charAt(0) == '.')) {
                line = line.substring(1);
            }
            lines = lines + "\n" + line;
        }
        return listaDeIds;
    }
   public static String getData(BufferedReader input) throws IOException {
       String lines = "";
       while(true){
           String line = input.readLine();
           if (line == null){
               throw new IOException("Socket.Socket.Server unawares closed the connection");
           }
           // siempre la respuesta del servidor que generalemente es largo, termina en punto
           if(line.equals(".")) {
               break;
           }
           if ((!line.isEmpty()) && (line.charAt(0) == '.')) {
               line = line.substring(1);
           }
           lines = lines + "\n" + line;
       }
       return lines;
   }

}
