import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
// TODO -> REFACTORIZAR
public class SMTPClient {
    private String server;
    private String receptorUser;
    private String  emisorUser;
    private int port;
    public SMTPClient(){
        this.server = SocketUtils.MAIL_SERVER;
        this.receptorUser = "muerte201469@gmail.com";
        this.emisorUser = "grupo29sc@tecnoweb.org.bo";
        this.port = SocketUtils.SMTP_PORT;
    }
    public SMTPClient(String receptorUser,
                      String emisorUser){
            this.server = SocketUtils.MAIL_SERVER;
            this.receptorUser = receptorUser;
            this.emisorUser = emisorUser;
            this.port = SocketUtils.SMTP_PORT;
    }
    public void executeSMTPClient() {
        try{
            System.out.println(this.toString());

            Socket socket = new Socket(this.getServer(),this.getPort());
            String command = "";
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            if ( SMTPClient.esEntradaValida(socket,input,output) ) {
                System.out.println("Mensaje del servidor: " + input.readLine());
                command = "HELO " + this.getServer() + " \r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a HELO: " + input.readLine());

                command = "MAIL FROM: " + this.getEmisorUser() + "\r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a Mail From: " + input.readLine());

                command = "RCPT TO: " + this.getReceptorUser() + "\r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a RCPT FROM: " + input.readLine());

                command = "DATA \r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a DATA: " + input.readLine());

                command = "Subject: DEMO VIA SOCKET'S\r\n";
                command += "\r\n";
                command += "Hola como estas\r\n";
                command += "bien... gracias.\r\n";
                command += ".\r\n";


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
    public static void main(String[] args) {
        SMTPClient smtpClient = new SMTPClient();
        smtpClient.executeSMTPClient();
    }

    public static boolean esEntradaValida(Socket socket, BufferedReader input, DataOutputStream output){
        return socket != null && input != null && output != null;
    }

    @Override
    public String toString() {
        return "SMTPClient{" +
                "server='" + server + '\'' +
                ", receptorUser='" + receptorUser + '\'' +
                ", emisorUser='" + emisorUser + '\'' +
                ", port=" + port +
                '}';
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
    public int getPort(){
        return this.port;
    }

}
