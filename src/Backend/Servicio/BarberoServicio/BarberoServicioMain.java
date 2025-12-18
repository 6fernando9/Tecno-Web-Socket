package Backend.Servicio.BarberoServicio;

import Backend.Servicio.CreateServicio.CreateServicioSQLQuery;
import Backend.Servicio.dto.CreateServicioDTO;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Database.PGSQLClient;
import SMTP.SMTPClient;
import Utils.SQLUtils;
import Utils.TecnoUtils;

public class BarberoServicioMain {
    public static void executeBarberoServicioDemon(String emisor,String receptor,String server,String subject){
        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);

        try{
            Resultado<String> barberoIdResultado = verificarErrores(subject);

            if(!barberoIdResultado.esExitoso()){
                smtpClientResponse.sendDataToServer("SQL Obtener Barbero con Servicios: Fallo Campos", barberoIdResultado.getError() + "\r\n");
                return;
            }
            String barberoId = barberoIdResultado.getValor();
            BarberoServicioSQL barberoServicioSQL = new BarberoServicioSQL();
            String barberos = barberoServicioSQL.executeGetBarberoConServicios(pgsqlClient,Long.parseLong(barberoId));
            smtpClientResponse.sendDataToServer("SQL Obtener Barbero con Servicios", barberos + "\r\n");
        }catch (Exception e){
            smtpClientResponse.sendDataToServer("SQL ERROR: Obtener Barbero con Servicios", e.getMessage() + "\r\n");
        }

    }
    private static Resultado<String> verificarErrores(String subject){
        String[] data = TecnoUtils.procesarString(subject);
        if(data.length < 1){
            return Resultado.error("Error..Se esperaba al menos un parametro (barbero_id)");
        }
        if(GeneralMethods.esCampoNuloVacio(data[0])){
            return Resultado.error("Error... el id no puede ser nulo");
        }
        Long barberoIdDto;
        try{
            barberoIdDto = Long.parseLong(data[0]);
        }catch (NumberFormatException e){
            return Resultado.error("Error.. dato no numerico");
        }
        String idBarbero = data[0];
        return Resultado.ok(idBarbero);
    }
}
