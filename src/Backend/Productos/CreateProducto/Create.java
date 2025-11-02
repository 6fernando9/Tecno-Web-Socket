package Backend.Productos.CreateProducto;

import Backend.Productos.dto.CreateProductoDTO;
import Backend.Utils.GeneralMethods.Resultado;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.Filtrador;
import Utils.SQLUtils;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.util.List;

public class Create {
    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                createproducto["estrellas","buenas estrellas","10","10","1"]
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
            Resultado<CreateProductoDTO> resultadoCreateProducto = CreateProductoDTO.createProductoFromSubject(subject);
            if(!resultadoCreateProducto.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Create Producto: Fallo Campos", resultadoCreateProducto.getError() + "\r\n");
                return;
            }
            CreateProductoDTO createProductoDTO = resultadoCreateProducto.getValor();
            CreateSQLQuery createSQLQuery = new CreateSQLQuery();
            String strCreateProducto = createSQLQuery.executeInsertProductoQuery(pgsqlClient,createProductoDTO);
            smtpClientResponse.sendDataToServer("SQL CreateProducto",strCreateProducto + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Create Producto","Fallo al crear Producto\r\n");
        }
    }
}
