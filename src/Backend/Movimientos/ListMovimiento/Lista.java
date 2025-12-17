package Backend.Movimientos.ListMovimiento;

import Backend.Movimientos.dto.ListMovimientoDTO;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.*;

import java.util.List;

public class Lista {
    public static void main(String[] args){
        String emisor = "2003miguelito.mgs@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
    // subject: movimiento_inventario_list["productoId","usuarioId","desde","hasta","tipo","motivo","limit"]
    // Use empty string "" for parameters you want to skip
    String subject = "movimiento_inventario_list[\"1\",\"\",\"2025-01-01\",\"2025-12-31\",\"\",\"Compra proveedor\",\"50\"]";
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
        Filtrador filtrador = new Filtrador(emisor,subject,context,dataList);
        boolean existeMensajeEnPop3 = filtrador.existeMensajeDelUsuario();
        System.out.println("existe el mensaje: " + existeMensajeEnPop3);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        if( existeMensajeEnPop3 ){
            try{
                ListSQLQuery sql = new ListSQLQuery();
                var resultado = ListMovimientoDTO.crearMedianteSubject(subject);
                if(!resultado.esExitoso()){
                    smtpClientResponse.sendDataToServer("SQL ListMovimientos", resultado.getError() + "\r\n");
                    return;
                }
                ListMovimientoDTO dto = resultado.getValor();
                String result = sql.listMovimientos(pgsqlClient,dto);
                smtpClientResponse.sendDataToServer("SQL ListMovimientos",result + "\r\n");
            }catch(Exception e){
                smtpClientResponse.sendDataToServer("SQL ListMovimientos","ERROR: " + e.getMessage() + "\r\n");
            }
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail List Movimiento","Fallo al listar movimientos\r\n");
        }

    }

    // Called by demon: parse subject, execute SQL and reply
    public static void executeListMovimientosDemon(String emisor, String receptor, String server, String subject){
        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);
        try{

            ListSQLQuery sql = new ListSQLQuery();
            var resultado = ListMovimientoDTO.crearMedianteSubject(subject);
            if(!resultado.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL ListMovimientos", resultado.getError() + "\r\n");
                return;
            }
            ListMovimientoDTO dto = resultado.getValor();
            String result = sql.listMovimientos(pgsqlClient,dto);
            smtpClientResponse.sendDataToServer("SQL ListMovimientos",result + "\r\n");
        }catch(Exception e){
            smtpClientResponse.sendDataToServer("SQL ListMovimientos","ERROR: " + e.getMessage() + "\r\n");
        }
    }
}
