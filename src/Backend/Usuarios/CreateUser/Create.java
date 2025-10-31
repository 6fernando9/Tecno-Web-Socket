package Backend.Usuarios.CreateUser;

import Backend.Usuarios.dto.UsuarioDTO;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.*;

import java.util.List;

public class Create {
    public static void main(String args[]){
        String emisor = "demo@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = "updateuser";
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
        List<String> mockList = MockMessage.obtenerListaMockMessage();
        //System.out.println(mockList);
        Filtrador filtrador = new Filtrador(emisor,subject,context,mockList);
        boolean existeMensajeEnPop3 = filtrador.existeMensajeDelUsuario();
        System.out.println(existeMensajeEnPop3);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        if( existeMensajeEnPop3 ){
            //TODO -> se puede crear id automaticamente con random o UUID
            UsuarioDTO usuarioDTO = new UsuarioDTO(1L,"Fernando Padilla Lopez","123333","fernando@gmail.com",7563872L,"admin");
            CreateSQLQuery createSQLQuery = new CreateSQLQuery();
            String queryCreateUser = createSQLQuery.getCreateUserQuery(usuarioDTO);
            String resultadoCreateUser = createSQLQuery.executePGSQLClientForPersonTableForPatronQuery(pgsqlClient,queryCreateUser);
            smtpClientResponse.sendDataToServer("SQL CreateUser",resultadoCreateUser);
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Create User","Fallo al crear Usuario");
        }

    }

}
