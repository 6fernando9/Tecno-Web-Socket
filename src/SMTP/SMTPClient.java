package SMTP;

import Utils.SocketUtils;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
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
        this.receptorUser = "fernando@gmail.com";
        this.emisorUser = "grupo30sc@tecnoweb.org.bo";
        this.port = SocketUtils.SMTP_PORT;
    }
    public SMTPClient(String emisorUser,
                      String receptorUser){
            this.server = SocketUtils.MAIL_SERVER;
            this.receptorUser = receptorUser;
            this.emisorUser = emisorUser;
            this.port = SocketUtils.SMTP_PORT;
    }
    public void executeSMTPClient() {
        try{
            Socket socket = new Socket(this.getServer(),this.getPort());
            String command = "";
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            if ( SocketUtils.esEntradaValida(socket,input,output) ) {
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
            SocketUtils.closeServices(socket,input,output);
        }catch(Exception e){
            System.out.println("throw - "+ e.getMessage());
        }
    }
    public static void executeMailFrom(String emisor,BufferedReader input,DataOutputStream output) throws IOException {
        String command = "MAIL FROM: " + emisor + "\r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a Mail From: " + input.readLine());
    }
    public static void executeReceivedTo(String receptor,BufferedReader input,DataOutputStream output) throws IOException {
        String command = "RCPT TO: " + receptor + "\r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a RCPT TO: " + input.readLine());
    }
    public static void executeData(BufferedReader input,DataOutputStream output) throws IOException {
        String command = "DATA \r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a DATA: " + input.readLine());
    }
    public static void executeOnlySubject(String subject,BufferedReader input,DataOutputStream output) throws IOException {
        String command = "SUBJECT: " + subject + "\r\n";
        command += ".\r\n";
        output.writeBytes(command);
        System.out.println("Respuesta servidor a Data Subject: " + input.readLine());
    }
    public static void executeDataSubject(String subject,String context,BufferedReader input,DataOutputStream output) throws IOException {
        String command = "SUBJECT: " + subject + "\r\n";
        command += "\r\n";
        if (context != null) {
            command += context + "\r\n";
        }
        command += ".\r\n";
        output.writeBytes(command);
        System.out.println("Respuesta servidor a Data Subject: " + input.readLine());
    }
    //para la tarea
    public void sendDataToServer(String subject,String context){
        try{
            Socket socket = new Socket(this.getServer(),this.getPort());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            if (SocketUtils.esEntradaValida(socket,input,output)) {
                executeMailFrom(this.getEmisorUser(),input,output);
                executeReceivedTo(this.getReceptorUser(),input,output);
                executeData(input,output);
                executeDataSubject(subject,context,input,output);
            }
            SocketUtils.closeServices(socket,input,output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        executeTask();
    }

    public static void executeTask() {
       SMTPClient smtpClient = new SMTPClient();
       smtpClient.executeSMTPClient();
    }


    // metodos que toda clase de JAVA tiene
    @Override
    public String toString() {
        return "SMTP.SMTPClient{" +
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
