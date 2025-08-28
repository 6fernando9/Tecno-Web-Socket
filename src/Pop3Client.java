import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
/// TODO -> REFACTORIZAR
/// TOOD -> Crear cliente para BD con posgreSQL
public class Pop3Client {
    private String server;
    private String user;
    private String password;
    private int port;
    public Pop3Client(){
        this.server = SocketUtils.MAIL_SERVER;
        this.user = "grupo30sc";
        this.password = "grup030grup030*";
        this.port = SocketUtils.POP3_PORT;
    }
    public Pop3Client(String user,
                      String password){
        this.server = SocketUtils.MAIL_SERVER;
        this.user = user;
        this.password = password;
        this.port = SocketUtils.POP3_PORT;
    }
    public void executePop3Client() {
        String line;
        String command;
        try{
            Socket socket = new Socket(this.getServer(),this.getPort());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            if ( SocketUtils.esEntradaValida(socket,input,output) ) {
                System.out.println("Mensaje del servidor: " + input.readLine());
                command = "USER " + this.getUser() + "\r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a USER: " + input.readLine());

                command = "PASS " + this.getPassword() + "\r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a PASS: " + input.readLine());

                command = "STAT\r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a STAT: " + input.readLine());

                command ="LIST \r\n";
                System.out.println("comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta del servidor: " + SocketUtils.getMultiline(input));

                command = "RETR 19\r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a RETR 19: " + SocketUtils.getMultiline(input));

                command = "QUIT\r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a QUIT: " + input.readLine());

            }
            output.close();
            input.close();
            socket.close();
        }catch(Exception e){
            System.out.println("throw - "+ e.getMessage());
        }
    }
    public static void main(String[] args) {
        Pop3Client pop3Client = new Pop3Client();
        pop3Client.executePop3Client();
    }



    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    //conexion con BD psql -U agenda -d db_agenda -h mail.tecnoweb.org.bo -c 'SELECT * FROM persona'

}
