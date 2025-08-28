import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketUtils {
    public static int SMTP_PORT = 25;
    public static int POP3_PORT = 110;
    public static String MAIL_SERVER = "mail.tecnoweb.org.bo";

    public static boolean esEntradaValida(Socket socket, BufferedReader input, DataOutputStream output){
        return socket != null && input != null && output != null;
    }
    static String getMultiline(BufferedReader input) throws IOException {
        String lines = "";
        while (true) {
            String line = input.readLine();
            if (line == null){
                throw new IOException("Server unawares closed the connection");
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
