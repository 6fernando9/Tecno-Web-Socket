package Backend.Citas.ConsultarCitas;

import Backend.Citas.dto.ConsultarBarberoDTO;
import Backend.Citas.dto.ConsultarServicioBarberoDTO;
import Backend.Usuarios.ListarUser.ListarSQLUser;
import Backend.Utils.GeneralMethods.Resultado;
import Database.PGSQLClient;
import SMTP.SMTPClient;
import Utils.SQLUtils;

public class ConsultarSQL {
    public static void executeConsultarServiciosDeBarberoMonto(String emisor,String receptor,String server,String subject){

        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);

        Resultado<ConsultarServicioBarberoDTO> resultadoMensajeDTO = ConsultarServicioBarberoDTO.crearMedianteSubject(subject);
        if (!resultadoMensajeDTO.esExitoso()) {
            smtpClientResponse.sendDataToServer("SQL Consultar Servicios de Barbero -  fallo en campos",resultadoMensajeDTO.getError() + "\r\n");
            return;
        }
        ConsultarServicioBarberoDTO dtoEntrada = resultadoMensajeDTO.getValor();
        ConsultarSQLQuery consultarSQL = new ConsultarSQLQuery();
        String resultList = consultarSQL.executeConsultarMontoDelServicioSolicitado(pgsqlClient, dtoEntrada);
        smtpClientResponse.sendDataToServer("SQL Consultar Servicios De Barbero",resultList + "\r\n");
    }

    public static void executeConsultarBarberosEnFecha(String emisor,String receptor,String server,String subject){

        PGSQLClient pgsqlClient = new PGSQLClient(server, SQLUtils.DB_GRUPO_USER,SQLUtils.DB_GRUPO_PASSWORD,SQLUtils.DB_GRUPO_DB_NAME);
        SMTPClient smtpClientResponse = new SMTPClient(server,receptor,emisor);

        Resultado<ConsultarBarberoDTO> resultadoMensajeDTO = ConsultarBarberoDTO.crearMedianteSubject(subject);
        if (!resultadoMensajeDTO.esExitoso()) {
            smtpClientResponse.sendDataToServer("SQL Consultar Barbero en Fecha -  fallo en campos",resultadoMensajeDTO.getError() + "\r\n");
            return;
        }
        ConsultarBarberoDTO dtoEntrada = resultadoMensajeDTO.getValor();
        ConsultarSQLQuery consultarSQL = new ConsultarSQLQuery();
        String resultList = consultarSQL.executeConsultarBarberosDisponibles(pgsqlClient, dtoEntrada);
        smtpClientResponse.sendDataToServer("SQL Consultar Barberos en Fecha",resultList + "\r\n");
    }
}
