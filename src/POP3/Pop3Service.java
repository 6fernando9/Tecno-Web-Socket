package POP3;

import Utils.SocketUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

public class Pop3Service {
    private static final String SERVER = "mail.tecnoweb.org.bo";
    private static final int PORT = 110;
    private static final String USER = "grupo14sc";
    private static final String PASS = "grup014grup014*";
    private Socket socket;
    private BufferedReader input;
    private DataOutputStream output;

    public void connect() throws IOException {
        this.socket = new Socket("mail.tecnoweb.org.bo", 110);
        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.output = new DataOutputStream(this.socket.getOutputStream());
        this.input.readLine();
        this.output.writeBytes("USER grupo14sc\r\n");
        this.input.readLine();
        this.output.writeBytes("PASS grup014grup014*\r\n");
        this.input.readLine();
    }

    public String getSubject(int mailNum) throws IOException {
        String mail = this.getMail(mailNum);
        String[] lines = mail.split("\n");

        for(String line : lines) {
            if (line.toLowerCase().startsWith("subject:")) {
                return line.substring(8).trim();
            }
        }

        return "";
    }

    public String getFrom(int mailNum) throws IOException {
        String mail = this.getMail(mailNum);
        String[] lines = mail.split("\n");

        for(String line : lines) {
            if (line.toLowerCase().startsWith("from:")) {
                String from = line.substring(5).trim();
                int startBracket = from.indexOf(60);
                int endBracket = from.indexOf(62);
                if (startBracket != -1 && endBracket != -1) {
                    from = from.substring(startBracket + 1, endBracket);
                } else if (from.contains("@")) {
                    String[] parts = from.split("\\s+");

                    for(String part : parts) {
                        if (part.contains("@")) {
                            from = part.replace("<", "").replace(">", "");
                            break;
                        }
                    }
                }

                return from;
            }
        }

        return "";
    }

    public int getMailCount() throws IOException {
        this.output.writeBytes("STAT\r\n");
        String response = this.input.readLine();
        String[] parts = response.split("\\s+");
        return parts.length >= 2 ? Integer.parseInt(parts[1]) : 0;
    }

    public void deleteMail(int mailNum) throws IOException {
        this.output.writeBytes("DELE " + mailNum + "\r\n");
        this.input.readLine();
    }

    public String getMail(int mailNum) throws IOException {
        this.output.writeBytes("RETR " + mailNum + "\r\n");
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = this.input.readLine()) != null && !line.equals(".")) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
    //para obtener la lista de id
    public List<String> obtenerListaDeIds() throws IOException {
        String command = "LIST \r\n";
        System.out.println("Comando: " + command);
        this.output.writeBytes(command);
        return SocketUtils.getIdsForListCommand(this.input);
    }
    public void close() throws IOException {
        this.output.writeBytes("QUIT\r\n");
        this.input.readLine();
        this.input.close();
        this.output.close();
        this.socket.close();
    }
    public void executeRetrCommand(int messageNumber) throws IOException {
        String command = "RETR " + messageNumber + "\r\n";
        System.out.println("Command: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta del servidor: " + SocketUtils.getMultiline(input));
    }

    public static void main(String[] args){
        Pop3Service pop3Service = new Pop3Service();
        try {
            pop3Service.connect();
            List<String> lista = pop3Service.obtenerListaDeIds();
            for (String id:lista ) {
                System.out.println(pop3Service.getFrom(Integer.parseInt(id)));
                System.out.println(pop3Service.getSubject(Integer.parseInt(id)));
            }
            pop3Service.close();
            System.out.println(lista);
        } catch (IOException e) {
            System.err.println("[Error..de pop3] " + e.getMessage());
            try{
                pop3Service.close();
            }catch (IOException er){
                System.err.println("[Error..de cierre de conexion] " + er.getMessage());
            }
        }
    }
}
