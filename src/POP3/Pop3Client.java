package POP3;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import Utils.SocketUtils;
import Utils.TecnoUtils;

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

    //para usarlo en el parcial
    public Pop3Client(String server,String user,String password){
        this.server = server;
        this.user = user;
        this.password = password;
        this.port = SocketUtils.POP3_PORT;
    }
    public Pop3Client(String user,
                      String password){
        this.server = SocketUtils.MAIL_SERVER;
        this.user = user;
        this.password = password;
        this.port = SocketUtils.POP3_PORT;
    }
    public void executeUserCommand(BufferedReader input, DataOutputStream output) throws IOException {
        String command = "USER " + this.getUser() + "\r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a USER: " + input.readLine());
    }
    public void executePassCommand(BufferedReader input, DataOutputStream output) throws IOException {
        String command = "PASS " + this.getPassword() + "\r\n";
        System.out.println("Command: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta del servidor: " + input.readLine());
    }

    public void executeStatCommand(BufferedReader input, DataOutputStream output) throws IOException {
        String command = "STAT\r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a STAT: " + input.readLine());
    }

    public void executeListCommand(BufferedReader input,DataOutputStream output) throws IOException{
        String command = "LIST \r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta del servidor LIST: " + input.readLine());
    }
    public void executeRetrCommand(int messageNumber,BufferedReader input,DataOutputStream output) throws IOException {
        String command = "RETR " + messageNumber + "\r\n";
        System.out.println("Command: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta del servidor: " + SocketUtils.getMultiline(input));
    }
    public void executeDeleCommand(int messageNumber,BufferedReader input, DataOutputStream output) throws IOException {
        String command = "DELE " + messageNumber + "\r\n";
        System.out.println("Command: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta del servidor: " + input.readLine());
    }
    public void executeQuitCommand(BufferedReader input, DataOutputStream output) throws IOException{
        String command = "QUIT\r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a QUIT: " + input.readLine());
    }

    private String executeRetrCommandForTask(int messageNumber,BufferedReader input,DataOutputStream output) throws IOException {
        String command = "RETR " + messageNumber + "\r\n";
        System.out.println("Command: " + command);
        output.writeBytes(command);
        //System.out.println("Respuesta del servidor: " + SocketUtils.getMultiline(input));
        return SocketUtils.getData(input);
    }
    //el List solo retorna el id y la dimension ejem:
    // 1  4449
    // 2  3332
    private List<String> executeListCommandForTask(BufferedReader input,DataOutputStream output) throws IOException {
        String command = "LIST \r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        return SocketUtils.getIdsForListCommand(input);
    }
    private List<String> cargarListaDeData(List<String> listaDeIds,BufferedReader input,DataOutputStream output) throws IOException {
        List<String> listaDeDatas = new ArrayList<>();
        for(String id: listaDeIds){
            String data = this.executeRetrCommandForTask(Integer.parseInt(id),input,output);
            listaDeDatas.add(data);
        }
        return listaDeDatas;
    }
    public void executeHelo(BufferedReader input,DataOutputStream output) throws IOException {
        String command = "HELO " + this.getServer() + " \r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a HELO: " + input.readLine());
    }

    public List<String> executeTaskPop3(){
        List<String> listaData = new ArrayList<>();
        try{
            Socket socket = new Socket(this.getServer(),this.getPort());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            if ( SocketUtils.esEntradaValida(socket,input,output) ) {
                System.out.println("Mensaje del servidor: " + input.readLine());
//                this.executeHelo(input,output);
                this.executeUserCommand(input,output);
                this.executePassCommand(input,output);
                List<String> listaIds = this.executeListCommandForTask(input,output);
                listaData = this.cargarListaDeData(listaIds,input,output);
                this.executeQuitCommand(input,output);
            }
            SocketUtils.closeServices(socket,input,output);
        }catch(Exception e){
            System.out.println("throw - "+ e.getMessage());
        }
        return listaData;
    }

    public void executePop3Client() {
        String command;
        try{
            //inicializar conexion
            Socket socket = new Socket(this.getServer(),this.getPort());
            //input para respuestas del servidor
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //output para envio de datos al servidor
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            if ( SocketUtils.esEntradaValida(socket,input,output) ) {
                System.out.println("Mensaje del servidor: " + input.readLine());
//                this.executeHelo(input,output);
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

                //para ver cada mensaje
                for (int i = 1; i < 5; i++) {
                    executeRetrCommand(i,input,output);
                }
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
    public void vaciarMensajesDeGrupo(){
        try{
            Socket socket = new Socket(this.getServer(),this.getPort());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            if ( SocketUtils.esEntradaValida(socket,input,output) ) {
                System.out.println("Mensaje del servidor: " + input.readLine());
//                this.executeHelo(input,output);
                this.executeUserCommand(input,output);
                this.executePassCommand(input,output);
                List<String> listaIds = this.executeListCommandForTask(input,output);
                for(String id: listaIds){
                    this.executeDeleCommand(Integer.parseInt(id),input,output);
                }
                this.executeQuitCommand(input,output);
            }
            SocketUtils.closeServices(socket,input,output);
        }catch(Exception e){
            System.out.println("throw - "+ e.getMessage());
        }

    }
    public static void main(String[] args) {
//        String emisor = "muerte201469@gmail.com";
//        String receptor = "grupo14sc@tecnoweb.org.bo";
//        String user = TecnoUtils.getUserForPop3(receptor);
//        System.out.println("Usuario");
//        System.out.println(user);
//        System.out.println("Password");
//        String password = TecnoUtils.generatePasswordForPop3(user);
//        System.out.println(password);
//        Pop3Client pop3Client = new Pop3Client(SocketUtils.MAIL_SERVER,user,password);
//        System.out.println(pop3Client.executeTaskPop3());

        //para vaciado
        Pop3Client pop3Client = new Pop3Client(SocketUtils.MAIL_SERVER,"grupo14sc","grup014grup014*");
        pop3Client.vaciarMensajesDeGrupo();

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
}
