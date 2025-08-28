import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
/// TODO -> REFACTORIZAR
/// TOOD -> Crear cliente para BD con posgreSQL
public class Pop3Client {
    private String server;
    private String user;
    private String password;
    private String line;
    private String command;
    private int port;
    public Pop3Client(){
        this.server = "mail.tecnoweb.org.bo";
        this.user = "grupo20sa";
        this.password = "grup020grup020*";
    }
    public Pop3Client(String server, String user,
                      String password, String line,String command, int port){
        this.server = server;
        this.user = user;
        this.password = password;
        this.command = command;
        this.line = line;
        this.port = port;
    }
    public static void main(String[] args) {
        String server = "mail.tecnoweb.org.bo";
        String user = "grupo29sa";
        String  password = "grup029grup029*";
        String line;
        String command;
        int port = 110;
        try{
            Socket socket = new Socket(server,port);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            if ( SMTPClient.esEntradaValida(socket,input,output) ) {
                System.out.println("Mensaje del servidor: " + input.readLine());
                command = "USER " + user + " \r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a USER: " + input.readLine());

                command = "PASS " + password + " \r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a PASS: " + input.readLine());

                command = "STAT\r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a STAT: " + input.readLine());

                command = "RETR 19\r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a RETR 19: " + getMultiline(input));

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
    static private String getMultiline(BufferedReader input) throws IOException {
        String lines = "";
        while (true) {
            String line = input.readLine();
            if (line == null){
                throw new IOException("Server unawares closed the connection");
            }
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static boolean esEntradaValida(Socket socket, BufferedReader input, DataOutputStream output){
        return socket != null && input != null && output != null;
    }
    //conexion con BD psql -U agenda -d db_agenda -h mail.tecnoweb.org.bo -c 'SELECT * FROM persona'

}
