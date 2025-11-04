package Backend.Reports.DetalleVentaPorCita;

import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.*;

import java.util.List;

public class DetalleVentaPorCita {
    public static void main(String[] args){
        String emisor = "2003miguelito.mgs@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        // subject: reporte_detalle_venta_cita[3]
        String subject = "reporte_detalle_venta_cita[3]";
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
                Integer citaId = (params.length > 0 && !params[0].isBlank()) ? Integer.parseInt(params[0]) : null;

                DetalleVentaPorCitaSQL sql = new DetalleVentaPorCitaSQL();
                String result = sql.run(pgsqlClient, citaId);
                smtpClientResponse.sendDataToServer("REPORTE Detalle Venta por Cita",result + "\r\n");
            }catch(Exception e){
                smtpClientResponse.sendDataToServer("REPORTE Detalle Venta por Cita","ERROR: " + e.getMessage() + "\r\n");
            }
        }else{
            smtpClientResponse.sendDataToServer("REPORTE Fail","Fallo al solicitar reporte\r\n");
        }

    }
}

    // Called by demon: parse subject, execute SQL and reply
    public static void executeDetalleVentaPorCitaDemon(String emisor, String receptor, String server, String subject){
        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        try{
            String[] params = TecnoUtils.procesarString(subject);
            Integer citaId = (params.length > 0 && !params[0].isBlank()) ? Integer.parseInt(params[0]) : null;
            DetalleVentaPorCitaSQL sql = new DetalleVentaPorCitaSQL();
            String result = sql.run(pgsqlClient, citaId);
            smtpClientResponse.sendDataToServer("REPORTE Detalle Venta por Cita",result + "\r\n");
        }catch(Exception e){
            smtpClientResponse.sendDataToServer("REPORTE Detalle Venta por Cita","ERROR: " + e.getMessage() + "\r\n");
        }
    }
