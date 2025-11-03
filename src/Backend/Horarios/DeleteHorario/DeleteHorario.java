package Backend.Horarios.DeleteHorario;

import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Backend.Utils.dto.ComparadorSigno;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.Filtrador;
import Utils.SQLUtils;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.util.List;

public class DeleteHorario {

    public static void executeDeleteHorarioDemon(String emisor,String receptor,String server,String subject){

        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        System.out.println("subject" + subject);

        Resultado<Long[]> resultadoVentaPago = ComparadorSigno.obtenerDobleIdFromSubject(subject);
        if(!resultadoVentaPago.esExitoso()){
            smtpClientResponse.sendDataToServer("SQL FAIL DELETE HORARIO: Fallo Campos", resultadoVentaPago.getError() + "\r\n");
            return;
        }
        Long[] barberoHorario = resultadoVentaPago.getValor();
        Long barberoId = barberoHorario[0];
        Long horarioId = barberoHorario[1];
        DeleteHorarioSQLQuery deleteHorarioSQLQuery = new DeleteHorarioSQLQuery();
        String strEliminarPago = deleteHorarioSQLQuery.executeEliminarHorario(pgsqlClient,barberoId,horarioId);
        smtpClientResponse.sendDataToServer("SQL Eliminar Horario con exito ", strEliminarPago + "\r\n");
    }
    public static void executeDeleteHorario(String emisor,String receptor,String server,String subject){
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

            Resultado<Long[]> resultadoVentaPago = ComparadorSigno.obtenerDobleIdFromSubject(subject);
            if(!resultadoVentaPago.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL FAIL DELETE HORARIO: Fallo Campos", resultadoVentaPago.getError() + "\r\n");
                return;
            }
            Long[] barberoHorario = resultadoVentaPago.getValor();
            Long barberoId = barberoHorario[0];
            Long horarioId = barberoHorario[1];
            DeleteHorarioSQLQuery deleteHorarioSQLQuery = new DeleteHorarioSQLQuery();
            String strEliminarPago = deleteHorarioSQLQuery.executeEliminarHorario(pgsqlClient,barberoId,horarioId);
            smtpClientResponse.sendDataToServer("SQL Eliminar Horario con exito ", strEliminarPago + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL FAIL DELETE HORARIO","Fallo al Eliminar el horario\r\n");
        }
    }
    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                eliminarHorarioDeBarbero["21","9"]
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

            Resultado<Long[]> resultadoVentaPago = ComparadorSigno.obtenerDobleIdFromSubject(subject);
            if(!resultadoVentaPago.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL FAIL DELETE HORARIO: Fallo Campos", resultadoVentaPago.getError() + "\r\n");
                return;
            }
            Long[] barberoHorario = resultadoVentaPago.getValor();
            Long barberoId = barberoHorario[0];
            Long horarioId = barberoHorario[1];
            DeleteHorarioSQLQuery deleteHorarioSQLQuery = new DeleteHorarioSQLQuery();
            String strEliminarPago = deleteHorarioSQLQuery.executeEliminarHorario(pgsqlClient,barberoId,horarioId);
            smtpClientResponse.sendDataToServer("SQL Eliminar Horario con exito ", strEliminarPago + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL FAIL DELETE HORARIO","Fallo al Eliminar el horario\r\n");
        }
    }

}
