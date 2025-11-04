package Backend.Reports.VentasTotales;

import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.*;

import java.util.List;

public class VentasTotales {
    public static void main(String[] args){
        String emisor = "2003miguelito.mgs@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        // subject: reporte_ventas_totales["2025-11-01","2025-11-30"]
        String subject = "reporte_ventas_totales[\"2025-11-01\",\"2025-11-30\"]";
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

                VentasTotalesSQL sql = new VentasTotalesSQL();
                String result = sql.run(pgsqlClient, desde, hasta);
                smtpClientResponse.sendDataToServer("REPORTE Ventas Totales",result + "\r\n");
            }catch(Exception e){
                smtpClientResponse.sendDataToServer("REPORTE Ventas Totales","ERROR: " + e.getMessage() + "\r\n");
            }
        }else{
            smtpClientResponse.sendDataToServer("REPORTE Fail","Fallo al solicitar reporte\r\n");
        }

    }
}

    // Called by demon: parse subject, execute SQL and reply
    public static void executeVentasTotalesDemon(String emisor, String receptor, String server, String subject){
        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        try{
            String[] params = TecnoUtils.procesarString(subject);
            String desde = (params.length > 0 && !params[0].isBlank()) ? params[0] : null;
            String hasta = (params.length > 1 && !params[1].isBlank()) ? params[1] : null;
            VentasTotalesSQL sql = new VentasTotalesSQL();
            String result = sql.run(pgsqlClient, desde, hasta);
            smtpClientResponse.sendDataToServer("REPORTE Ventas Totales",result + "\r\n");
        }catch(Exception e){
            smtpClientResponse.sendDataToServer("REPORTE Ventas Totales","ERROR: " + e.getMessage() + "\r\n");
        }
    }
