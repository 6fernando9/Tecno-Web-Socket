package Backend.Citas.UpdateCita;

import Backend.Citas.dto.UpdateCitaDTO;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.*;

import java.util.List;

public class Update {
    public static void main(String[] args){
        String emisor = "2003miguelito.mgs@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        // subject example: cita_update["12","123","45","2025-11-05T10:00","2025-11-05T10:30"]
        String subject = "cita_update[\"12\",\"123\",\"45\",\"2025-11-05T10:00\",\"2025-11-05T10:30\"]";
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
                var resultado = UpdateCitaDTO.crearMedianteSubject(subject);
                if(!resultado.esExitoso()){
                    smtpClientResponse.sendDataToServer("SQL Update Cita: Fallo Campos",resultado.getError() + "\r\n");
                    return;
                }
                UpdateCitaDTO dto = resultado.getValor();
                UpdateSQLQuery sql = new UpdateSQLQuery();
                String str = sql.updateCita(pgsqlClient, dto.citaId, dto.usuarioId, dto.barberoId, dto.fechaHoraInicio, dto.fechaHoraFin);
                smtpClientResponse.sendDataToServer("SQL UpdateCita",str + "\r\n");
            }catch(Exception e){
                smtpClientResponse.sendDataToServer("SQL UpdateCita","ERROR: " + e.getMessage() + "\r\n");
            }
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Update Cita","Fallo al actualizar cita\r\n");
        }

    }
}
