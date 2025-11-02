package Backend.Citas.ListCitas;

import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.*;

import java.util.List;

public class Lista {
    public static void main(String[] args){
        String emisor = "2003miguelito.mgs@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        // subject: cita_list["clienteId","barberoId","estado","2025-11-01T00:00","2025-11-30T23:59","servicioId","limit"]
        String subject = "cita_list[\"\",\"\",\"\",\"2025-11-01T00:00\",\"2025-11-30T23:59\",\"\",\"50\"]";
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
                String[] params = TecnoUtils.procesarString(subject);
                Long clienteId = (params.length > 0 && !params[0].isBlank()) ? Long.parseLong(params[0]) : null;
                Long barberoId = (params.length > 1 && !params[1].isBlank()) ? Long.parseLong(params[1]) : null;
                String estado = (params.length > 2 && !params[2].isBlank()) ? params[2] : null;
                String desde = (params.length > 3 && !params[3].isBlank()) ? params[3] : null;
                String hasta = (params.length > 4 && !params[4].isBlank()) ? params[4] : null;
                Long servicioId = (params.length > 5 && !params[5].isBlank()) ? Long.parseLong(params[5]) : null;
                Integer limit = null;
                if (params.length > 6 && !params[6].isBlank()) limit = Integer.parseInt(params[6]);

                ListSQLQuery sql = new ListSQLQuery();
                String result = sql.listCitas(pgsqlClient, clienteId, barberoId, estado, desde, hasta, servicioId, limit);
                smtpClientResponse.sendDataToServer("SQL ListCitas",result + "\r\n");
            }catch(Exception e){
                smtpClientResponse.sendDataToServer("SQL ListCitas","ERROR: " + e.getMessage() + "\r\n");
            }
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail List Cita","Fallo al listar citas\r\n");
        }

    }
}
