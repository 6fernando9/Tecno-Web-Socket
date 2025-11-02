package Backend.Movimientos.CreateMovimiento;

import Backend.Movimientos.dto.CreateMovimientoDTO;
import Database.PGSQLClient;
import POP3.Pop3Client;
import SMTP.SMTPClient;
import Utils.*;

import java.util.List;

public class Create {
    public static void main(String[] args){
    String emisor = "2003miguelito.mgs@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String subject = "movimiento_inventario_create[\"1\",\"2\",\"ingreso\",\"10\",\"Compra proveedor X\"]";
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
                var resultado = CreateMovimientoDTO.crearMedianteSubject(subject);
                if(!resultado.esExitoso()){
                    smtpClientResponse.sendDataToServer("SQL Create Movimiento: Fallo Campos",resultado.getError() + "\r\n");
                    return;
                }
                CreateMovimientoDTO dto = resultado.getValor();
                CreateSQLQuery sql = new CreateSQLQuery();
                String str = sql.executeInsertMovimientoAndApplyStock(pgsqlClient, dto);
                smtpClientResponse.sendDataToServer("SQL CreateMovimiento",str + "\r\n");
            }catch(Exception e){
                smtpClientResponse.sendDataToServer("SQL CreateMovimiento","ERROR: " + e.getMessage() + "\r\n");
            }
        }else{
            smtpClientResponse.sendDataToServer("SQL Fail Create Movimiento","Fallo al crear movimiento\r\n");
        }

    }
}
