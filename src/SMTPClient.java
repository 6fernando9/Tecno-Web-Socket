import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
// TODO -> REFACTORIZAR
public class SMTPClient {
    private String server;
    private String receptorUser;
    private String  emisorUser;
    private String line;
    private String command;
    private int port;
    public SMTPClient(){
        this.server = "mail.tecnoweb.org.bo";
        this.receptorUser = "grupo20sc@tecnoweb.org.bo";
        this.emisorUser = "evansbv@gmail.com";
    }
    public SMTPClient(String server, String receptorUser,
                      String emisorUser, String line,String command, int port){
            this.server = server;
            this.receptorUser = receptorUser;
            this.emisorUser = emisorUser;
            this.command = command;
            this.line = line;
            this.port = port;
    }
    public static void main(String[] args) {
        String server = "mail.tecnoweb.org.bo";
        String receptorUser = "grupo20sc@tecnoweb.org.bo";
        String  emisorUser = "evansbv@gmail.com";
        String line;
        String command;
        int port = 25;
        try{
            Socket socket = new Socket(server,port);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            if ( SMTPClient.esEntradaValida(socket,input,output) ) {
                System.out.println("Mensaje del servidor: " + input.readLine());
                command = "HELO " + server + " \r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a HELO: " + input.readLine());

                command = "MAIL FROM: <" + emisorUser + "> \r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a Mail From: " + input.readLine());

                command = "RCPT TO: <" + receptorUser + "> \r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a RCPT FROM: " + input.readLine());

                command = "DATA \r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a DATA: " + input.readLine());

                command = "SUBJECT : DEMO VIA SOCKET'S \n";
                command += "Hola como estas \n";
                command += "bien... gracias.\n";
                command += ".\n";

                System.out.println("comando: "+ command);
                output.writeBytes( command );
                System.out.println("Respuesta servidor a DATA: " + input.readLine());

                command = "QUIT \r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta Servidor: " + input.readLine());
            }
            output.close();
            input.close();
            socket.close();
        }catch(Exception e){
            System.out.println("throw - "+ e.getMessage());
        }
    }

    public static boolean esEntradaValida(Socket socket, BufferedReader input, DataOutputStream output){
        return socket != null && input != null && output != null;
    }
    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getReceptorUser() {
        return receptorUser;
    }

    public void setReceptorUser(String receptorUser) {
        this.receptorUser = receptorUser;
    }

    public String getEmisorUser() {
        return emisorUser;
    }

    public void setEmisorUser(String emisorUser) {
        this.emisorUser = emisorUser;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
