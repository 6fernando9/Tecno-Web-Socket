package Tarea;

import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.SQLUtils;
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
        SMTPClient smtpClient = new SMTPClient("fernando@gmail.com","grupo30sc@tecnoweb.org.bo");
         smtpClient.sendDataToServer("e",null);
        String user = TecnoUtils.getUserForPop3(smtpClient.getReceptorUser());
        String password = TecnoUtils.generatePasswordForPop3(user);
        Pop3Client pop3Client = new Pop3Client(user,password);
        List<String> dataList = pop3Client.executeTaskPop3();
        String patron = obtenerPatron(dataList,smtpClient.getEmisorUser());
        System.out.println(patron);
        PGSQLClient pgsqlClient = new PGSQLClient();
        System.out.println(SQLUtils.getQueryForPatron(patron));
        String data = pgsqlClient.executePGSQLClientForPersonTableForPatronQuery(SQLUtils.getQueryForPatron(patron));
        System.out.println(data);
        smtpClient.sendDataToServer("SQL INJECTION",data);
    }   
}
