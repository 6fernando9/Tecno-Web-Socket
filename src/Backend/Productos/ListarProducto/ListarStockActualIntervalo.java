package Backend.Productos.ListarProducto;

import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.dto.ComparadorSigno;
import Backend.Utils.GeneralMethods.Resultado;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.Filtrador;
import Utils.SQLUtils;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.util.List;
//lista respecto del stock_actual
public class ListarStockActualIntervalo {
    public static void executeListarStockActualIntervaloDemon(String emisor,String receptor,String server,String subject){
        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        System.out.println("subject" + subject);
        Resultado<int[]> resultadoListaSimple = ComparadorSigno.crearIntervaloFromSubject(subject);
        if(!resultadoListaSimple.esExitoso()){
            smtpClientResponse.sendDataToServer("SQL Listar Productos: Fallo Campos", resultadoListaSimple.getError() + "\r\n");
            return;
        }
        int[] comparadorSigno = resultadoListaSimple.getValor();
        System.out.println(comparadorSigno);
        ListarStockActualSQLQuery listarStockActualSQLQuery = new ListarStockActualSQLQuery();
        String strListarProducto = listarStockActualSQLQuery.executeListarProductosBetween(pgsqlClient,comparadorSigno);
        smtpClientResponse.sendDataToServer("SQL Listar Productos ",strListarProducto + "\r\n");
    }
    public static void executeListarStockActualIntervalo(String emisor,String receptor,String server,String subject){
        //subject = GeneralMethods.parsearSubjectComillaTriple(subject);
        String context = null;
        //TecnoUtils.validarCorreosDeUsuario(emisor,receptor);
        SMTPClient smtpClient = new SMTPClient(server,emisor,receptor);
        smtpClient.sendDataToServer(subject,context);
        System.out.println(smtpClient.getReceptorUser());
        String user = TecnoUtils.getUserForPop3(smtpClient.getReceptorUser());
        System.out.println("Usuario");
        System.out.println(user);
        System.out.println("Password");
        String password = TecnoUtils.generatePasswordForPop3(user);
        System.out.println(password);
        Pop3Client pop3Client = new Pop3Client(server,user,password);

        List<String> dataList = pop3Client.executeTaskPop3();

        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        Filtrador filtrador = new Filtrador(emisor,subject,context,dataList);
        boolean existeMensajeEnPop3 = filtrador.existeMensajeDelUsuario();
        System.out.println("existe el mensaje: " + existeMensajeEnPop3);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);

        if( existeMensajeEnPop3 ){
            System.out.println("subject" + subject);

            Resultado<int[]> resultadoListaSimple = ComparadorSigno.crearIntervaloFromSubject(subject);
            if(!resultadoListaSimple.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Listar Productos: Fallo Campos", resultadoListaSimple.getError() + "\r\n");
                return;
            }
            int[] comparadorSigno = resultadoListaSimple.getValor();
            System.out.println(comparadorSigno);
            ListarStockActualSQLQuery listarStockActualSQLQuery = new ListarStockActualSQLQuery();
            String strListarProducto = listarStockActualSQLQuery.executeListarProductosBetween(pgsqlClient,comparadorSigno);
            smtpClientResponse.sendDataToServer("SQL Listar Productos ",strListarProducto + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Listar Producto","Fallo al Listar Producto\r\n");
        }
    }
    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                listarproductosintervalo["10","15"]
                """;
        subject = GeneralMethods.parsearSubjectComillaTriple(subject);
        String context = null;
        String server = SocketUtils.MAIL_SERVER;
        TecnoUtils.validarCorreosDeUsuario(emisor,receptor);
        SMTPClient smtpClient = new SMTPClient(server,emisor,receptor);
        smtpClient.sendDataToServer(subject,context);
        System.out.println(smtpClient.getReceptorUser());
        String user = TecnoUtils.getUserForPop3(smtpClient.getReceptorUser());
        System.out.println("Usuario");
        System.out.println(user);
        System.out.println("Password");
        String password = TecnoUtils.generatePasswordForPop3(user);
        System.out.println(password);
        Pop3Client pop3Client = new Pop3Client(server,user,password);

        List<String> dataList = pop3Client.executeTaskPop3();

        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        Filtrador filtrador = new Filtrador(emisor,subject,context,dataList);
        boolean existeMensajeEnPop3 = filtrador.existeMensajeDelUsuario();
        System.out.println("existe el mensaje: " + existeMensajeEnPop3);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);

        if( existeMensajeEnPop3 ){
            System.out.println("subject" + subject);

            Resultado<int[]> resultadoListaSimple = ComparadorSigno.crearIntervaloFromSubject(subject);
            if(!resultadoListaSimple.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Listar Productos: Fallo Campos", resultadoListaSimple.getError() + "\r\n");
                return;
            }
            int[] comparadorSigno = resultadoListaSimple.getValor();
            System.out.println(comparadorSigno);
            ListarStockActualSQLQuery listarStockActualSQLQuery = new ListarStockActualSQLQuery();
            String strListarProducto = listarStockActualSQLQuery.executeListarProductosBetween(pgsqlClient,comparadorSigno);
            smtpClientResponse.sendDataToServer("SQL Listar Productos ",strListarProducto + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Listar Producto","Fallo al Listar Producto\r\n");
        }
    }
}
