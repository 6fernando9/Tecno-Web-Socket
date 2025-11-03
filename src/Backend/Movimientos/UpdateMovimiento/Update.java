package Backend.Movimientos.UpdateMovimiento;

import Backend.Movimientos.dto.UpdateMovimientoDTO;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.*;

import java.util.List;

public class Update {
    public static void main(String[] args){
        String emisor = "2003miguelito.mgs@gmail.com"; // using same requestor email pattern
        String receptor = "grupo14sc@tecnoweb.org.bo";
        // subject: movimiento_inventario_update["movement_id","cantidad?","motivo?","fecha?"]
        String subject = """
                    movimiento_inventario_update["45","40","Corrección manual","2025-11-02"]
                """;
        subject = GeneralMethods.parsearSubjectComillaTriple(subject);
        //String subject = "movimiento_inventario_update[\"45\",\"40\",\"Corrección manual\",\"2025-11-02\"]";
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
                var resultado = UpdateMovimientoDTO.crearMedianteSubject(subject);
                if(!resultado.esExitoso()){
                    smtpClientResponse.sendDataToServer("SQL Update Movimiento: Fallo Campos",resultado.getError() + "\r\n");
                    return;
                }
                UpdateMovimientoDTO dto = resultado.getValor();
                UpdateSQLQuery sql = new UpdateSQLQuery();
                String str = sql.executeUpdateMovimiento(pgsqlClient, dto);
                smtpClientResponse.sendDataToServer("SQL UpdateMovimiento",str + "\r\n");
            }catch(Exception e){
                smtpClientResponse.sendDataToServer("SQL UpdateMovimiento","ERROR: " + e.getMessage() + "\r\n");
            }
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Update Movimiento","Fallo al actualizar movimiento\r\n");
        }
    }
}
