package Backend.Horarios.ListarHorario;

import Backend.Pagos.ListPagos.ListarPagoDeVentaSQLQuery;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Backend.Utils.dto.IdentificadorPrimarioDTO;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.Filtrador;
import Utils.SQLUtils;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.util.List;

public class ListarHorarioDeBarbero {
    public static void executeListarHorarioDeBarbero(String emisor,String receptor,String server,String subject){
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
            Resultado<IdentificadorPrimarioDTO> resultadoDeleteDTO = IdentificadorPrimarioDTO.createDeleteUsuarioDTO(subject);
            if(!resultadoDeleteDTO.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Fail Listar Horario de Barbero: fallo en campos",resultadoDeleteDTO.getError() + "\r\n");
                return;
            }
            IdentificadorPrimarioDTO identificadorPrimarioDTO = resultadoDeleteDTO.getValor();
            ListarHorarioDeBarberoSQLQuery listarHorarioDeBarberoSQLQuery = new ListarHorarioDeBarberoSQLQuery();
            String resultadoListado = listarHorarioDeBarberoSQLQuery.executeListarHorarioDeBarbero(pgsqlClient, identificadorPrimarioDTO.id);
            smtpClientResponse.sendDataToServer("SQL Listar Horario de Barbero",resultadoListado + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Listar Horario de Barbero","Fallo al Listar el horario de barbero\r\n");
        }
    }
    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                listarHorarioDeBarbero["21"]
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
            Resultado<IdentificadorPrimarioDTO> resultadoDeleteDTO = IdentificadorPrimarioDTO.createDeleteUsuarioDTO(subject);
            if(!resultadoDeleteDTO.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Fail Listar Horario de Barbero: fallo en campos",resultadoDeleteDTO.getError() + "\r\n");
                return;
            }
            IdentificadorPrimarioDTO identificadorPrimarioDTO = resultadoDeleteDTO.getValor();
            ListarHorarioDeBarberoSQLQuery listarHorarioDeBarberoSQLQuery = new ListarHorarioDeBarberoSQLQuery();
            String resultadoListado = listarHorarioDeBarberoSQLQuery.executeListarHorarioDeBarbero(pgsqlClient, identificadorPrimarioDTO.id);
            smtpClientResponse.sendDataToServer("SQL Listar Horario de Barbero",resultadoListado + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Listar Horario de Barbero","Fallo al Listar el horario de barbero\r\n");
        }
    }

}
