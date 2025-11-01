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
    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                updateuser["83","XXX","XXXXXX","33333","Sxxxx@gmail.com","37563872","florencio"]
                """;
        subject = subject.replace("\r", "").replace("\n", " ");
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
            UpdateUsuarioDTO usuarioDTO = UpdateUsuarioDTO.crearUpdateUsuarioMedianteSubject(subject);
            UpdateSQLQuery updateSQLQuery = new UpdateSQLQuery();
            String resultadoCreateUser = updateSQLQuery.executeUpdateUserQuery(pgsqlClient, usuarioDTO);
            smtpClientResponse.sendDataToServer("SQL Update User",resultadoCreateUser);
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Update User","Fallo al actualizar Usuario\r\n");
        }

    }
}
