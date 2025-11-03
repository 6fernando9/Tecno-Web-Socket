package Backend.Servicio.UpdateServicio;

import Backend.Servicio.dto.UpdateServicioDTO;
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

public class UpdateServicio {
    public static void executeUpdateServicioDemon(String emisor,String receptor,String server,String subject){
        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);

        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        Resultado<UpdateServicioDTO> resultadoServicioDTO = UpdateServicioDTO.crearUpdateServicioMedianteSubject(subject);
        if(!resultadoServicioDTO.esExitoso()){
            smtpClientResponse.sendDataToServer("SQL Update Servicio: Fallo Campos", resultadoServicioDTO.getError() + "\r\n");
            return;
        }
        UpdateServicioDTO updateServicioDTO = resultadoServicioDTO.getValor();
        UpdateServicioSQLQuery updateServicioSQLQuery = new UpdateServicioSQLQuery();
        String strCreateServicio = updateServicioSQLQuery.executeUpdateServicio(pgsqlClient, updateServicioDTO);
        smtpClientResponse.sendDataToServer("SQL Update Servicio", strCreateServicio + "\r\n");



    }
    public static void executeUpdateServicio(String emisor,String receptor,String server,String subject){
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
        //List<String> mockList = MockMessage.obtenerListaMockMessage();
        //System.out.println(mockList);
        Filtrador filtrador = new Filtrador(emisor,subject,context,dataList);
        boolean existeMensajeEnPop3 = filtrador.existeMensajeDelUsuario();
        System.out.println("existe el mensaje: " + existeMensajeEnPop3);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        if( existeMensajeEnPop3 ){
            Resultado<UpdateServicioDTO> resultadoServicioDTO = UpdateServicioDTO.crearUpdateServicioMedianteSubject(subject);
            if(!resultadoServicioDTO.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Update Servicio: Fallo Campos", resultadoServicioDTO.getError() + "\r\n");
                return;
            }
            UpdateServicioDTO updateServicioDTO = resultadoServicioDTO.getValor();
            UpdateServicioSQLQuery updateServicioSQLQuery = new UpdateServicioSQLQuery();
            String strCreateServicio = updateServicioSQLQuery.executeUpdateServicio(pgsqlClient, updateServicioDTO);
            smtpClientResponse.sendDataToServer("SQL Update Servicio", strCreateServicio + "\r\n");

        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Update Servicio","Fallo al crear Servicio\r\n");
        }
    }
    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                createServicio["1","corte alizado","buenas servicio","10","30"]
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
        //List<String> mockList = MockMessage.obtenerListaMockMessage();
        //System.out.println(mockList);
        Filtrador filtrador = new Filtrador(emisor,subject,context,dataList);
        boolean existeMensajeEnPop3 = filtrador.existeMensajeDelUsuario();
        System.out.println("existe el mensaje: " + existeMensajeEnPop3);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        if( existeMensajeEnPop3 ){
            Resultado<UpdateServicioDTO> resultadoServicioDTO = UpdateServicioDTO.crearUpdateServicioMedianteSubject(subject);
            if(!resultadoServicioDTO.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Update Servicio: Fallo Campos", resultadoServicioDTO.getError() + "\r\n");
                return;
            }
            UpdateServicioDTO updateServicioDTO = resultadoServicioDTO.getValor();
            UpdateServicioSQLQuery updateServicioSQLQuery = new UpdateServicioSQLQuery();
            String strCreateServicio = updateServicioSQLQuery.executeUpdateServicio(pgsqlClient, updateServicioDTO);
            smtpClientResponse.sendDataToServer("SQL Update Servicio", strCreateServicio + "\r\n");

        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Update Servicio","Fallo al crear Servicio\r\n");
        }
    }
}
