package Backend.Usuarios.CreateUser;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.Resultado;
import Backend.Usuarios.dto.CreateUsuarioDTO;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.*;

import java.util.List;

public class Create {
    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                createuser["fercho","fernando ","fernando@gmail.com","111111","12345678","cliente"]
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
        //List<String> mockList = MockMessage.obtenerListaMockMessage();
        //System.out.println(mockList);
        Filtrador filtrador = new Filtrador(emisor,subject,context,dataList);
        boolean existeMensajeEnPop3 = filtrador.existeMensajeDelUsuario();
        System.out.println("existe el mensaje: " + existeMensajeEnPop3);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        if( existeMensajeEnPop3 ){
            Resultado<CreateUsuarioDTO> resultadoCreateUser = CreateUsuarioDTO.crearUsuarioMedianteSubject(subject);
            if(!resultadoCreateUser.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Create User: Fallo Campos",resultadoCreateUser.getError() + "\r\n");
                return;
            }
            CreateUsuarioDTO createUsuarioDTO = resultadoCreateUser.getValor();
            CreateSQLQuery createSQLQuery = new CreateSQLQuery();

            String strCreateUser = createSQLQuery.executeInsertUserQuery(pgsqlClient, createUsuarioDTO);
            smtpClientResponse.sendDataToServer("SQL CreateUser",strCreateUser + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Create User","Fallo al crear Usuario\r\n");
        }

    }

}
