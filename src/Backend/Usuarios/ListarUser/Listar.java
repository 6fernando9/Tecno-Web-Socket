package Backend.Usuarios.ListarUser;

import Backend.Usuarios.Resultado;
import Backend.Usuarios.dto.MensajeUsuarioDTO;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.Filtrador;
import Utils.SQLUtils;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.util.List;

public class Listar {
    public static void main(String[] args) {
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                listarusuario["*"]
                """;
        //pa evitar la cagada de /r/n
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
            Resultado<MensajeUsuarioDTO> resultadoMensajeDTO = MensajeUsuarioDTO.createMensajePatronDTO(subject);
            if (!resultadoMensajeDTO.esExitoso()) {
                smtpClientResponse.sendDataToServer("SQL ListUser -  fallo en campos",resultadoMensajeDTO.getError() + "\r\n");
                return;
            }
            MensajeUsuarioDTO mensajeUsuarioDTO = resultadoMensajeDTO.getValor();
            ListarSQLUser listarSQLUser = new ListarSQLUser();
            String resultList = listarSQLUser.executeListarUsuarios(pgsqlClient,mensajeUsuarioDTO.message);
            smtpClientResponse.sendDataToServer("SQL ListUser",resultList + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail List User","Fallo al crear Usuario\r\n");
        }
    }
}
