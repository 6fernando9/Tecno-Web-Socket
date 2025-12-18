package Backend.Servicio.BarberoServicio;

import Backend.Roles;
import Backend.Servicio.dto.UpdateServicioDTO;
import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.BarberoServicioDTO;
import Backend.Usuarios.dto.UpdateUsuarioDTO;
import Database.PGSQLClient;

import java.sql.*;
import java.util.HashSet;

public class BarberoServicioSQL {
    public String executeGetBarberoConServicios(PGSQLClient pgsqlClient, Long barberoId) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        BarberoServicioDTO barberoDTO = null;

        try (Connection conn = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Conexión exitosa para obtener barbero con servicios.");
            UpdateUsuarioDTO dto = GeneralUsuarioSQLUtils.findUserById(conn,barberoId);
            if(!dto.rol.equalsIgnoreCase(Roles.BARBERO.getDescripcion())){
                return "Error.. el usuario no tiene el rol de barbero";
            }
            //aqui ya sse hace la consulta a la tabla barbero, no hay necesidad de aplicar otra consulta
            barberoDTO = GeneralUsuarioSQLUtils.findBarberoConServiciosById(conn, barberoId);
            if (barberoDTO == null) {
                return "Barbero no encontrado";
            }
        } catch (SQLException e) {
            System.out.println("Error en BarberoServicioSQL: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
        return formatearRespuesta(barberoDTO);
    }

    public String formatearRespuesta(BarberoServicioDTO dto) {
        if (dto == null) return "No se encontró el barbero o no tiene servicios asignados.";

        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\r\n");
        sb.append("PERFIL DEL BARBERO\r\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\r\n");
        sb.append("ID: ").append(dto.id).append("\r\n");
        sb.append("Nombre: ").append(dto.nombre).append(" ").append(dto.apellido).append("\r\n");
        sb.append("Email: ").append(dto.email).append("\r\n");
        sb.append("Teléfono: ").append(dto.telefono).append("\r\n");
        sb.append("\r\n✨ SERVICIOS QUE BRINDA:\r\n");

        int i = 1;
        for (UpdateServicioDTO s : dto.servicios) {
            sb.append(String.format("  %d. [%d] %s - Bs. %.2f (%d min)\r\n",
                    i++, s.id, s.nombre, s.precio, s.duracion));
        }
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\r\n");

        return sb.toString();
    }

}
