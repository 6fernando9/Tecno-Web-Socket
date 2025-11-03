package Backend.Usuarios.UpdateUser;

import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Backend.Usuarios.dto.UpdateUsuarioDTO;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.Filtrador;
import Utils.SQLUtils;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.util.List;
//TODO -> MEJORAR FORMATO DE SALIDA O RESPUESTA
//ACTUALIZA BIEN
//TIENE VALIDACION DE PASSWORD
//TIENE VALIDACION DE NULOS
//TIENE VALIDACION DE GRUPOS
//TIENE VALIDACION DE EMAIL UNICO
//TIENE VALIDACION DE USUARIO QUE NO EXISTE
public class UpdateUsuario {
    public static void executeUpdateUsuarioDemon(String emisor,String receptor,String server,String subject){
        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        Resultado<UpdateUsuarioDTO> resultadoUpdate = UpdateUsuarioDTO.crearUpdateUsuarioMedianteSubject(subject);
        if(!resultadoUpdate.esExitoso()){
            smtpClientResponse.sendDataToServer("SQL Update User: Fallo de campos",resultadoUpdate.getError() + "\r\n");
            return;
        }
        UpdateUsuarioDTO updateUsuarioDTO = resultadoUpdate.getValor();
        UpdateSQLQuery updateSQLQuery = new UpdateSQLQuery();
        String resultadoCreateUser = updateSQLQuery.executeUpdateUserQuery(pgsqlClient, updateUsuarioDTO);
        smtpClientResponse.sendDataToServer("SQL Update User",resultadoCreateUser + "\r\n");
    }
    public static void executeUpdateUsuario(String emisor,String receptor,String server,String subject){
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
            Resultado<UpdateUsuarioDTO> resultadoUpdate = UpdateUsuarioDTO.crearUpdateUsuarioMedianteSubject(subject);
            if(!resultadoUpdate.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Update User: Fallo de campos",resultadoUpdate.getError() + "\r\n");
                return;
            }
            UpdateUsuarioDTO updateUsuarioDTO = resultadoUpdate.getValor();
            UpdateSQLQuery updateSQLQuery = new UpdateSQLQuery();
            String resultadoCreateUser = updateSQLQuery.executeUpdateUserQuery(pgsqlClient, updateUsuarioDTO);
            smtpClientResponse.sendDataToServer("SQL Update User",resultadoCreateUser + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Update User","Fallo al actualizar Usuario\r\n");
        }
    }
    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                updateuser["22","flores2","flore flores","flores@gmail.com","3333333","12345678","barbero"]
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
            Resultado<UpdateUsuarioDTO> resultadoUpdate = UpdateUsuarioDTO.crearUpdateUsuarioMedianteSubject(subject);
            if(!resultadoUpdate.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Update User: Fallo de campos",resultadoUpdate.getError() + "\r\n");
                return;
            }
            UpdateUsuarioDTO updateUsuarioDTO = resultadoUpdate.getValor();
            UpdateSQLQuery updateSQLQuery = new UpdateSQLQuery();
            String resultadoCreateUser = updateSQLQuery.executeUpdateUserQuery(pgsqlClient, updateUsuarioDTO);
            smtpClientResponse.sendDataToServer("SQL Update User",resultadoCreateUser + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Update User","Fallo al actualizar Usuario\r\n");
        }

    }
}
