package Backend.Reports.CitasPorBarbero;

import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.*;

import java.util.List;

public class CitasPorBarbero {
    public static void main(String[] args){
        String emisor = "2003miguelito.mgs@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        // subject: reporte_citas_barbero["2025-11-01","2025-11-30"]
        String subject = "reporte_citas_barbero[\"2025-11-01\",\"2025-11-30\"]";
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
                String desde = (params.length > 0 && !params[0].isBlank()) ? params[0] : null;
                String hasta = (params.length > 1 && !params[1].isBlank()) ? params[1] : null;

                CitasPorBarberoSQL sql = new CitasPorBarberoSQL();
                String result = sql.run(pgsqlClient, desde, hasta);
                smtpClientResponse.sendDataToServer("REPORTE Citas por Barbero",result + "\r\n");
            }catch(Exception e){
                smtpClientResponse.sendDataToServer("REPORTE Citas por Barbero","ERROR: " + e.getMessage() + "\r\n");
            }
        }else{
            smtpClientResponse.sendDataToServer("REPORTE Fail","Fallo al solicitar reporte\r\n");
        }

    }
}
