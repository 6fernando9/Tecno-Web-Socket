package Backend.Productos.UpdateProducto;

import Backend.Productos.dto.UpdateProductoDTO;
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

public class Update {

    public static void main(String[] args){
        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = """
                updateproducto["2","ESTRELLAS","ESTRELLAS BUENAS","1000","102","10"]
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
            Resultado<UpdateProductoDTO> resultadoUpdateDto = UpdateProductoDTO.createUpdateProductoDTO(subject);
            if(!resultadoUpdateDto.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Update Producto: Fallo Campos", resultadoUpdateDto.getError() + "\r\n");
                return;
            }
            UpdateProductoDTO updateProductoDTO = resultadoUpdateDto.getValor();
            UpdateSQLQuery updateSQLQuery = new UpdateSQLQuery();
            String strUpdateProducto = updateSQLQuery.executeUpdateProductoQuery(pgsqlClient, updateProductoDTO);
            smtpClientResponse.sendDataToServer("SQL UpdateProducto", strUpdateProducto + "\r\n");
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Update Producto","Fallo al Actualizar Producto\r\n");
        }
    }
}
