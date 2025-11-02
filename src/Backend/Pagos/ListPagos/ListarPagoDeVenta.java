package Backend.Pagos.ListPagos;

import Backend.Productos.ListarProducto.ListarStockActualSQLQuery;
import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.Resultado;
import Backend.Usuarios.dto.DeleteUsuarioDTO;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.Filtrador;
import Utils.SQLUtils;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.util.List;

public class ListarPagoDeVenta {
    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                listarPagosDeVenta["22"]
                """;
        subject = GeneralUsuarioSQLUtils.parsearSubjectComillaTriple(subject);
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

            Resultado<DeleteUsuarioDTO> resultadoListaSimple = DeleteUsuarioDTO.createDeleteUsuarioDTO(subject);
            if(!resultadoListaSimple.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Listar Pago De venta: Fallo Campos", resultadoListaSimple.getError() + "\r\n");
                return;
            }
            DeleteUsuarioDTO deleteUsuarioDTO = resultadoListaSimple.getValor();
            ListarPagoDeVentaSQLQuery listarPagoDeVentaSQLQuery = new ListarPagoDeVentaSQLQuery();
            String str = listarPagoDeVentaSQLQuery.executeListarPagosDeVentaQuery(pgsqlClient,deleteUsuarioDTO.id);
            smtpClientResponse.sendDataToServer("SQL Listar Productos ",str + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Listar Producto","Fallo al Listar Producto\r\n");
        }
    }
}
