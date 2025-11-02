package Backend.Productos.ListarProducto;

import Backend.Productos.dto.ComparadorSigno;
import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.Resultado;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.Filtrador;
import Utils.SQLUtils;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.util.List;
//lista respecto del stock_actual
public class ListarStockActualSimple {
    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                listarproductossimple[">=4"]
                """;
        String listarTodosCommand = """
                listarproductossimple["*"]
                """;
        subject = GeneralUsuarioSQLUtils.parsearSubjectComillaTriple(subject);
        listarTodosCommand = GeneralUsuarioSQLUtils.parsearSubjectComillaTriple(listarTodosCommand);
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
            if(subject.equals(listarTodosCommand)){
                ComparadorSigno comparadorSigno = null;
                ListarStockActualSQLQuery listarStockActualSQLQuery = new ListarStockActualSQLQuery();
                String strListarProducto = listarStockActualSQLQuery.executeListarProductos(pgsqlClient,comparadorSigno);
                smtpClientResponse.sendDataToServer("SQL Listar Productos ",strListarProducto + "\r\n");
                return;
            }
            Resultado<ComparadorSigno> resultadoListaSimple = ComparadorSigno.crearComparadorFromSubject(subject);
            if(!resultadoListaSimple.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Listar Productos: Fallo Campos", resultadoListaSimple.getError() + "\r\n");
                return;
            }
            ComparadorSigno comparadorSigno = resultadoListaSimple.getValor();
            System.out.println(comparadorSigno);
            ListarStockActualSQLQuery listarStockActualSQLQuery = new ListarStockActualSQLQuery();
            String strListarProducto = listarStockActualSQLQuery.executeListarProductos(pgsqlClient,comparadorSigno);
            smtpClientResponse.sendDataToServer("SQL Listar Productos ",strListarProducto + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Listar Producto","Fallo al Listar Producto\r\n");
        }
    }
}
