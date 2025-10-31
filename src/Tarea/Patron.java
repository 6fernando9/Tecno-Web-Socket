package Tarea;

import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.SQLUtils;
import Utils.SocketUtils;
import Utils.TecnoUtils;
import java.util.List;

public class Patron {

    private static String obtenerPatron(List<String> listaDeData, String remitente){
        for(String data:listaDeData){
            if (data.contains("From: " + remitente)) {
                String busqueda = "SUBJECT: ";
                int index = data.indexOf(busqueda);
                return data.substring(index + busqueda.length(),data.length());
            }
        }
        return null;
    }

    public static void main(String[] args) {
        // send to patron with SMTP
        String emisor = "fernandopadilla170@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String server = SocketUtils.MAIL_SERVER;
        TecnoUtils.validarCorreosDeUsuario(emisor,receptor);

        SMTPClient smtpClient = new SMTPClient(server,emisor,receptor);
        smtpClient.sendDataToServer("e",null);

        String user = TecnoUtils.getUserForPop3(smtpClient.getReceptorUser());
        String password = TecnoUtils.generatePasswordForPop3(user);
        Pop3Client pop3Client = new Pop3Client(server,user,password);

        List<String> dataList = pop3Client.executeTaskPop3();
        String patron = obtenerPatron(dataList,smtpClient.getEmisorUser());
        PGSQLClient pgsqlClient = new PGSQLClient(server,SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        System.out.println(SQLUtils.getQueryForPatron(patron));
        String data = pgsqlClient.executePGSQLClientForPersonTableForPatronQuery(SQLUtils.getQueryForPatron(patron));
        System.out.println(data);

    }   
}
