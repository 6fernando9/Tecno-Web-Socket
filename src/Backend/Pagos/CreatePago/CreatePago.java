package Backend.Pagos.CreatePago;

import Backend.Pagos.dto.PagoDTO;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.Filtrador;
import Utils.SQLUtils;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.util.List;

public class CreatePago {

    public static void executeCrearPagoDemon(String emisor,String receptor,String server,String subject){

        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        System.out.println("subject" + subject);
        Resultado<PagoDTO> resultadoCreatePagoDto = PagoDTO.crearPagoFromSubject(subject);
        if(!resultadoCreatePagoDto.esExitoso()){
            smtpClientResponse.sendDataToServer("SQL Update Pago De venta: Fallo Campos", resultadoCreatePagoDto.getError() + "\r\n");
            return;
        }
        PagoDTO pagoDTO = resultadoCreatePagoDto.getValor();
        CreatePagoSQLQuery createPagoSQLQuery = new CreatePagoSQLQuery();
        String str = createPagoSQLQuery.executeCrearPago(pgsqlClient, pagoDTO);
        smtpClientResponse.sendDataToServer("SQL Crear Pago ",str + "\r\n");
    }
    public static void executeCrearPago(String emisor,String receptor,String server,String subject){
        //subject = GeneralMethods.parsearSubjectComillaTriple(subject);
        String context = null;
        //TecnoUtils.validarCorreosDeUsuario(emisor,receptor);
        SMTPClient smtpClient = new SMTPClient(server,emisor,receptor);
        smtpClient.sendDataToServer(subject,context);
        System.out.println(smtpClient.getReceptorUser());
        String user = TecnoUtils.getUserForPop3(smtpClient.getReceptorUser());
        System.out.println("Usuario");
        System.out.println(user);
        System.out.println("Password");
        String password = TecnoUtils.generatePasswordForPop3(user);
        System.out.println(password);
        Pop3Client pop3Client = new Pop3Client(server,user,password);

        List<String> dataList = pop3Client.executeTaskPop3();

        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        Filtrador filtrador = new Filtrador(emisor,subject,context,dataList);
        boolean existeMensajeEnPop3 = filtrador.existeMensajeDelUsuario();
        System.out.println("existe el mensaje: " + existeMensajeEnPop3);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);

        if( existeMensajeEnPop3 ){
            System.out.println("subject" + subject);

            Resultado<PagoDTO> resultadoCreatePagoDto = PagoDTO.crearPagoFromSubject(subject);
            if(!resultadoCreatePagoDto.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Update Pago De venta: Fallo Campos", resultadoCreatePagoDto.getError() + "\r\n");
                return;
            }
            PagoDTO pagoDTO = resultadoCreatePagoDto.getValor();
            CreatePagoSQLQuery createPagoSQLQuery = new CreatePagoSQLQuery();
            String str = createPagoSQLQuery.executeCrearPago(pgsqlClient, pagoDTO);
            smtpClientResponse.sendDataToServer("SQL Crear Pago ",str + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Crear Pago","Fallo al Crear Pago\r\n");
        }
    }
    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                crearPagoParaVenta["3","contado","25"]
                """;
        subject = GeneralMethods.parsearSubjectComillaTriple(subject);
        String context = null;
        String server = SocketUtils.MAIL_SERVER;
        TecnoUtils.validarCorreosDeUsuario(emisor,receptor);
        SMTPClient smtpClient = new SMTPClient(server,emisor,receptor);
        smtpClient.sendDataToServer(subject,context);
        System.out.println(smtpClient.getReceptorUser());
        String user = TecnoUtils.getUserForPop3(smtpClient.getReceptorUser());
        System.out.println("Usuario");
        System.out.println(user);
        System.out.println("Password");
        String password = TecnoUtils.generatePasswordForPop3(user);
        System.out.println(password);
        Pop3Client pop3Client = new Pop3Client(server,user,password);

        List<String> dataList = pop3Client.executeTaskPop3();

        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        Filtrador filtrador = new Filtrador(emisor,subject,context,dataList);
        boolean existeMensajeEnPop3 = filtrador.existeMensajeDelUsuario();
        System.out.println("existe el mensaje: " + existeMensajeEnPop3);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);

        if( existeMensajeEnPop3 ){
            System.out.println("subject" + subject);

            Resultado<PagoDTO> resultadoCreatePagoDto = PagoDTO.crearPagoFromSubject(subject);
            if(!resultadoCreatePagoDto.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Update Pago De venta: Fallo Campos", resultadoCreatePagoDto.getError() + "\r\n");
                return;
            }
            PagoDTO pagoDTO = resultadoCreatePagoDto.getValor();
            CreatePagoSQLQuery createPagoSQLQuery = new CreatePagoSQLQuery();
            String str = createPagoSQLQuery.executeCrearPago(pgsqlClient, pagoDTO);
            smtpClientResponse.sendDataToServer("SQL Crear Pago ",str + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Crear Pago","Fallo al Crear Pago\r\n");
        }
    }
}
