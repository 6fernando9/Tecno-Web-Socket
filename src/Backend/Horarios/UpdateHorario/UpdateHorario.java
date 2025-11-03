package Backend.Horarios.UpdateHorario;

import Backend.Horarios.dto.HorarioUpdateDTO;
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

public class UpdateHorario {
    public static void executeUpdateHorarioDemon(String emisor,String receptor,String server,String subject){

        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);

        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        Resultado<HorarioUpdateDTO> resultadoUpdate = HorarioUpdateDTO.createUpdateHorarioDtoFromSubject(subject);
        if(!resultadoUpdate.esExitoso()){
            smtpClientResponse.sendDataToServer("SQL Fail Update Horario: Fallo Campos", resultadoUpdate.getError() + "\r\n");
            return;
        }
        HorarioUpdateDTO horarioDto = resultadoUpdate.getValor();
        UpdateHorarioSQLQuery updateHorarioSQLQuery = new UpdateHorarioSQLQuery();
        String strUpdateUser = updateHorarioSQLQuery.executeUpdateHorarioQuery(pgsqlClient,horarioDto);
        smtpClientResponse.sendDataToServer("SQL Update Horario",strUpdateUser + "\r\n");
    }
    public static void executeUpdateHorario(String emisor,String receptor,String server,String subject){
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
            Resultado<HorarioUpdateDTO> resultadoUpdate = HorarioUpdateDTO.createUpdateHorarioDtoFromSubject(subject);
            if(!resultadoUpdate.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Fail Update Horario: Fallo Campos", resultadoUpdate.getError() + "\r\n");
                return;
            }
            HorarioUpdateDTO horarioDto = resultadoUpdate.getValor();
            UpdateHorarioSQLQuery updateHorarioSQLQuery = new UpdateHorarioSQLQuery();
            String strUpdateUser = updateHorarioSQLQuery.executeUpdateHorarioQuery(pgsqlClient,horarioDto);
            smtpClientResponse.sendDataToServer("SQL Update Horario",strUpdateUser + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Update Horario","Fallo al hacer update de Horario\r\n");
        }
    }
    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        //barbero_id,horario_id
        String subject = """
                updateHorario["21","10","18:00","22:00"]
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
            Resultado<HorarioUpdateDTO> resultadoUpdate = HorarioUpdateDTO.createUpdateHorarioDtoFromSubject(subject);
            if(!resultadoUpdate.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Fail Update Horario: Fallo Campos", resultadoUpdate.getError() + "\r\n");
                return;
            }
            HorarioUpdateDTO horarioDto = resultadoUpdate.getValor();
            UpdateHorarioSQLQuery updateHorarioSQLQuery = new UpdateHorarioSQLQuery();
            String strUpdateUser = updateHorarioSQLQuery.executeUpdateHorarioQuery(pgsqlClient,horarioDto);
            smtpClientResponse.sendDataToServer("SQL Update Horario",strUpdateUser + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Update Horario","Fallo al hacer update de Horario\r\n");
        }
    }
}
