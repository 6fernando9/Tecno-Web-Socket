package Backend.Usuarios.UpdateUser;

import Backend.Usuarios.CreateUser.CreateSQLQuery;
import Backend.Usuarios.dto.UpdateUsuarioDTO;
import Backend.Usuarios.dto.UsuarioDTO;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.Filtrador;
import Utils.SQLUtils;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.util.List;

public class Update {
    public static void main(String args[]){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = "updateuser";
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
        //List<String> mockList = MockMessage.obtenerListaMockMessage();
        //System.out.println(mockList);
        Filtrador filtrador = new Filtrador(emisor,subject,context,dataList);
        boolean existeMensajeEnPop3 = filtrador.existeMensajeDelUsuario();
        System.out.println("existe el mensaje: " + existeMensajeEnPop3);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        if( existeMensajeEnPop3 ){
            //TODO -> extraer informacion de insercion de usuario [,,,,,],pasar a DTO
            UpdateUsuarioDTO usuarioDTO = new UpdateUsuarioDTO("TTR","SSSSSS","123333","SSS@gmail.com",7563872L,"admin");

            UpdateSQLQuery updateSQLQuery = new UpdateSQLQuery();
            String queryUpdateUser = updateSQLQuery.getCreateUserQuery(usuarioDTO);
            System.out.println("Query: "+ queryUpdateUser);
            String resultadoCreateUser = updateSQLQuery.executeUpdateUserQuery(pgsqlClient, queryUpdateUser);
            smtpClientResponse.sendDataToServer("SQL Update User",resultadoCreateUser);
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Update User","Fallo al actualizar Usuario\r\n");
        }

    }
}
