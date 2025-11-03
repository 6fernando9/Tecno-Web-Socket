package SMTP;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Utils.SocketUtils;
import Utils.TecnoUtils;

public class SMTPClient {
    private String server;
    private String receptorUser;
    private String  emisorUser;
    private int port;

    //para usarlo en el parcial
    public SMTPClient(String server, String emisorUser,String receptorUser){
        this.server = server;
        this.receptorUser = receptorUser;
        this.emisorUser = emisorUser;
        this.port = SocketUtils.SMTP_PORT;
    }
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
    //execute for HTML MEssage

    public void executeSMTPClientHTML() {
        try {
            Socket socket = new Socket(this.getServer(), this.getPort());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            if (SocketUtils.esEntradaValida(socket, input, output)) {
                System.out.println("Mensaje del servidor: " + input.readLine());

                // 🔹 HELO
                output.writeBytes("HELO " + this.getServer() + "\r\n");
                System.out.println("Respuesta servidor a HELO: " + input.readLine());

                // 🔹 MAIL FROM
                output.writeBytes("MAIL FROM: " + this.getEmisorUser() + "\r\n");
                System.out.println("Respuesta servidor a MAIL FROM: " + input.readLine());

                // 🔹 RCPT TO
                output.writeBytes("RCPT TO: " + this.getReceptorUser() + "\r\n");
                System.out.println("Respuesta servidor a RCPT TO: " + input.readLine());

                // 🔹 DATA
                output.writeBytes("DATA\r\n");
                System.out.println("Respuesta servidor a DATA: " + input.readLine());

                // 🔹 Encabezados MIME + cuerpo HTML
                String htmlMessage =
                        "MIME-Version: 1.0\r\n" +
                                "Content-Type: text/html; charset=UTF-8\r\n" +
                                "Subject: 📨 Correo HTML desde Java\r\n" +
                                "\r\n" +
                                "<html>\r\n" +
                                "<body style=\"font-family: Arial, sans-serif;\">\r\n" +
                                "  <h2 style=\"color:#4CAF50;\">✅ Usuario creado exitosamente</h2>\r\n" +
                                "  <p><b>Nombre:</b> Evans Balcázar</p>\r\n" +
                                "  <p><b>Email:</b> evans@gmail.com</p>\r\n" +
                                "  <p><b>Teléfono:</b> 76773834</p>\r\n" +
                                "  <p><b>Rol:</b> barbero</p>\r\n" +
                                "  <hr>\r\n" +
                                "  <p>Bienvenido al sistema 🎉</p>\r\n" +
                                "</body>\r\n" +
                                "</html>\r\n" +
                                ".\r\n";

                output.writeBytes(htmlMessage);
                System.out.println("Respuesta servidor a HTML: " + input.readLine());

                // 🔹 QUIT
                output.writeBytes("QUIT\r\n");
                System.out.println("Respuesta servidor a QUIT: " + input.readLine());
            }

            SocketUtils.closeServices(socket, input, output);
        } catch (Exception e) {
            System.out.println("throw - " + e.getMessage());
        }
    }

    public void executeMailFrom(BufferedReader input,DataOutputStream output) throws IOException {
        String command = "MAIL FROM: " + this.getEmisorUser() + "\r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a Mail From: " + input.readLine());
    }
    public void executeHelo(BufferedReader input,DataOutputStream output) throws IOException {
        String command = "HELO " + this.getServer() + " \r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a HELO: " + input.readLine());
    }
    public void executeReceivedTo(BufferedReader input,DataOutputStream output) throws IOException {
        String command = "RCPT TO: " + this.getReceptorUser() + "\r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a RCPT TO: " + input.readLine());
    }
    public void executeData(BufferedReader input,DataOutputStream output) throws IOException {
        String command = "DATA \r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a DATA: " + input.readLine());
    }
    public void executeOnlySubject(String subject,BufferedReader input,DataOutputStream output) throws IOException {
        String command = "SUBJECT: " + subject + "\r\n";
        command += ".\r\n";
        output.writeBytes(command);
        System.out.println("Respuesta servidor a Data Subject: " + input.readLine());
    }
    public void executeDataSubject(String subject,String context,BufferedReader input,DataOutputStream output) throws IOException {
        String command = "SUBJECT: " + subject + "\r\n";
        command += "\r\n";
        if (context != null) {
            command += context + "\r\n";
        }
        command += ".\r\n";
        System.out.println("comando: "+ command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a Data Subject: " + input.readLine());
    }

    public void executeQuitCommand(BufferedReader input, DataOutputStream output) throws IOException{
        String command = "QUIT\r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a QUIT: " + input.readLine());
    }
    //antes de usar el metodo requiero instanciar el emisor y receptor
    public void sendDataToServer(String subject,String context){
        try{
            Socket socket = new Socket(this.getServer(),this.getPort());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            if (SocketUtils.esEntradaValida(socket,input,output)) {
                this.executeHelo(input,output);
                this.executeMailFrom(input,output);
                this.executeReceivedTo(input,output);
                this.executeData(input,output);
                this.executeDataSubject(subject,context,input,output);
                this.executeQuitCommand(input,output);
            }
            SocketUtils.closeServices(socket,input,output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }








    public static void main(String[] args) {
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
//        String receptor = "muerte201469@gmail.com";
//        String emisor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                listarproductossimple[">=15"]
                """;
        subject = GeneralMethods.parsearSubjectComillaTriple(subject);
        //String subject = "createuser[\"8\",\"ZSZ\",\"SZSZSZ\",\"123333\",\"SSS@gmail.com\",\"7563872\",\"admin\"]";
        //subject = GeneralMethods.parsearSubjectComillaTriple(subject);
        String context = null;
        String server = SocketUtils.MAIL_SERVER;
        TecnoUtils.validarCorreosDeUsuario(emisor,receptor);
        SMTPClient smtpClient = new SMTPClient(server,emisor,receptor);
        smtpClient.sendDataToServer(subject,context);
        //smtpClient.executeSMTPClientHTML();
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
