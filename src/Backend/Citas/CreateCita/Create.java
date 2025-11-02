package Backend.Citas.CreateCita;

import Backend.Citas.dto.CreateCitaDTO;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.*;

import java.util.List;

public class Create {
    public static void main(String[] args){
        String emisor = "2003miguelito.mgs@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        // subject example:
        // cita_create["123","45","1,2","2025-11-03T14:30","2025-11-03T15:00","Observacion","10.00"]
        String subject = "cita_create[\"123\",\"45\",\"1,2\",\"2025-11-03T14:30\",\"2025-11-03T15:00\",\"Corte urgente\",\"10.00\"]";
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
                var resultado = CreateCitaDTO.crearMedianteSubject(subject);
                if(!resultado.esExitoso()){
                    smtpClientResponse.sendDataToServer("SQL Create Cita: Fallo Campos",resultado.getError() + "\r\n");
                    return;
                }
                CreateCitaDTO dto = resultado.getValor();
                CreateSQLQuery sql = new CreateSQLQuery();
                String str = sql.executeCreateCita(pgsqlClient, dto);
                smtpClientResponse.sendDataToServer("SQL CreateCita",str + "\r\n");
            }catch(Exception e){
                smtpClientResponse.sendDataToServer("SQL CreateCita","ERROR: " + e.getMessage() + "\r\n");
            }
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Create Cita","Fallo al crear cita\r\n");
        }

    }
}
