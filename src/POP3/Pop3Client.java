package POP3;

import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
    public void executeUserCommand(String user,BufferedReader input, DataOutputStream output) throws IOException {
        String command = "USER " + this.getUser() + "\r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a USER: " + input.readLine());
    }
    public void executePassCommand(String password,BufferedReader input, DataOutputStream output) throws IOException {
        String command = "PASS " + password + "\r\n";
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
        return SocketUtils.getData(input);
    }
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


    public List<String> executeTaskPop3(){
        List<String> listaData = new ArrayList<>();
        try{
            Socket socket = new Socket(this.getServer(),this.getPort());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            if ( SocketUtils.esEntradaValida(socket,input,output) ) {
                System.out.println("Mensaje del servidor: " + input.readLine());
                this.executeUserCommand(this.getUser(),input,output);
                this.executePassCommand(this.getPassword(),input,output);
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
}
