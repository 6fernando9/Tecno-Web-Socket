package Tarea;

import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.SQLUtils;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.util.List;

public class TestExamen {
    public static void Main(String[] args){
        // send to patron with SMTP
        String emisor = "jorgito123@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = "e";
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
        //necesito filtrar por emisor y obtener el ultimo mensaje

        //String patron = obtenerPatron(dataList,smtpClient.getEmisorUser());

        //System.out.println(SQLUtils.getQueryForPatron(patron));
//        String data = pgsqlClient.executePGSQLClientForPersonTableForPatronQuery(SQLUtils.getQueryForPatron(patron));
//        System.out.println(data);
    }
}
