package Backend.Servicio.ListarServicio;

import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Backend.Utils.dto.ComparadorSigno;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.Filtrador;
import Utils.SQLUtils;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.util.List;
//lista respecto del stock_actual
public class ListarServicioPrecioSimple {
    public static void executeListarServicioPrecioSimple(String emisor,String receptor,String server,String subject){

        String listarTodosCommand = """
                listarServicioSimple["*"]
                """;
        //subject = GeneralMethods.parsearSubjectComillaTriple(subject);
        listarTodosCommand = GeneralMethods.parsearSubjectComillaTriple(listarTodosCommand);
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
            System.out.println("perfect" + listarTodosCommand);
            if(subject.equalsIgnoreCase(listarTodosCommand)){
                ComparadorSigno comparadorSigno = null;
                ListarServicioPrecioSQLQuery listarServicioPrecioSQLQuery = new ListarServicioPrecioSQLQuery();
                String strListarProducto = listarServicioPrecioSQLQuery.executeListarServicios(pgsqlClient,comparadorSigno);
                smtpClientResponse.sendDataToServer("SQL Listar Servicios ",strListarProducto + "\r\n");
                return;
            }
            Resultado<ComparadorSigno> resultadoListaSimple = ComparadorSigno.crearComparadorFromSubject(subject);
            if(!resultadoListaSimple.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Listar Servicios: Fallo Campos", resultadoListaSimple.getError() + "\r\n");
                return;
            }
            ComparadorSigno comparadorSigno = resultadoListaSimple.getValor();
            ListarServicioPrecioSQLQuery listarServicioPrecioSQLQuery = new ListarServicioPrecioSQLQuery();
            String strListarProducto = listarServicioPrecioSQLQuery.executeListarServicios(pgsqlClient,comparadorSigno);
            smtpClientResponse.sendDataToServer("SQL Listar Servicios ",strListarProducto + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Listar Servicios","Fallo al Listar Servicios\r\n");
        }
    }
    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                listarServicioSimple["<15"]
                """;
        String listarTodosCommand = """
                listarServicioSimple["*"]
                """;
        subject = GeneralMethods.parsearSubjectComillaTriple(subject);
        listarTodosCommand = GeneralMethods.parsearSubjectComillaTriple(listarTodosCommand);
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
            System.out.println("perfect" + listarTodosCommand);
            if(subject.equalsIgnoreCase(listarTodosCommand)){
                ComparadorSigno comparadorSigno = null;
                ListarServicioPrecioSQLQuery listarServicioPrecioSQLQuery = new ListarServicioPrecioSQLQuery();
                String strListarProducto = listarServicioPrecioSQLQuery.executeListarServicios(pgsqlClient,comparadorSigno);
                smtpClientResponse.sendDataToServer("SQL Listar Servicios ",strListarProducto + "\r\n");
                return;
            }
            Resultado<ComparadorSigno> resultadoListaSimple = ComparadorSigno.crearComparadorFromSubject(subject);
            if(!resultadoListaSimple.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Listar Servicios: Fallo Campos", resultadoListaSimple.getError() + "\r\n");
                return;
            }
            ComparadorSigno comparadorSigno = resultadoListaSimple.getValor();
            ListarServicioPrecioSQLQuery listarServicioPrecioSQLQuery = new ListarServicioPrecioSQLQuery();
            String strListarProducto = listarServicioPrecioSQLQuery.executeListarServicios(pgsqlClient,comparadorSigno);
            smtpClientResponse.sendDataToServer("SQL Listar Servicios ",strListarProducto + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Listar Servicios","Fallo al Listar Servicios\r\n");
        }
    }
}
