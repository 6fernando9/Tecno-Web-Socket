package Backend.Citas.CancelCita;

import Backend.Citas.dto.CancelCitaDTO;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.*;

import java.util.List;

public class Cancel {
    public static void main(String[] args){
        String emisor = "2003miguelito.mgs@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        // subject example: cita_cancel["12","123","Motivo de cancelacion"]
        String subject = "cita_cancel[\"12\",\"123\",\"Cambio de planes\"]";
        String context = null;
        String server = SocketUtils.MAIL_SERVER;
        TecnoUtils.validarCorreosDeUsuario(emisor,receptor);
        SMTPClient smtpClient = new SMTPClient(server,emisor,receptor);
        smtpClient.sendDataToServer(subject,context);

        String user = TecnoUtils.getUserForPop3(smtpClient.getReceptorUser());
        String password = TecnoUtils.generatePasswordForPop3(user);
        Pop3Client pop3Client = new Pop3Client(server,user,password);

        List<String> dataList = pop3Client.executeTaskPop3();

        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        Filtrador filtrador = new Filtrador(emisor,subject,context,dataList);
        boolean existeMensajeEnPop3 = filtrador.existeMensajeDelUsuario();
        System.out.println("existe el mensaje: " + existeMensajeEnPop3);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        if( existeMensajeEnPop3 ){
            try{
                var resultado = CancelCitaDTO.crearMedianteSubject(subject);
                if(!resultado.esExitoso()){
                    smtpClientResponse.sendDataToServer("SQL Cancel Cita: Fallo Campos",resultado.getError() + "\r\n");
                    return;
                }
                CancelCitaDTO dto = resultado.getValor();
                CancelSQLQuery sql = new CancelSQLQuery();
                String str = sql.cancelarCita(pgsqlClient, dto.citaId, dto.usuarioId, dto.motivo);
                smtpClientResponse.sendDataToServer("SQL CancelCita",str + "\r\n");
            }catch(Exception e){
                smtpClientResponse.sendDataToServer("SQL CancelCita","ERROR: " + e.getMessage() + "\r\n");
            }
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Cancel Cita","Fallo al cancelar cita\r\n");
        }

    }

    // Called by the demon: parse subject, execute SQL and reply
    public static void executeCancelCitaDemon(String emisor, String receptor, String server, String subject){
        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        try{
            var resultado = CancelCitaDTO.crearMedianteSubject(subject);
            if(!resultado.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Cancel Cita: Fallo Campos",resultado.getError() + "\r\n");
                return;
            }
            CancelCitaDTO dto = resultado.getValor();
            CancelSQLQuery sql = new CancelSQLQuery();
            String str = sql.cancelarCita(pgsqlClient, dto.citaId, dto.usuarioId, dto.motivo);
            smtpClientResponse.sendDataToServer("SQL CancelCita",str + "\r\n");
        }catch(Exception e){
            smtpClientResponse.sendDataToServer("SQL CancelCita","ERROR: " + e.getMessage() + "\r\n");
        }
    }
}
