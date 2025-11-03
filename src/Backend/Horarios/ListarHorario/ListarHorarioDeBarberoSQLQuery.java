package Backend.Horarios.ListarHorario;

import Backend.Horarios.GeneralHorarioSQL;
import Backend.Horarios.dto.HorarioDTO;
import Backend.Horarios.dto.UsuarioHorarioDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;

public class ListarHorarioDeBarberoSQLQuery {

    public String executeListarHorarioDeBarbero(PGSQLClient pgsqlClient, Long id) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {

            System.out.println("Connecting successfully to database");

            // Buscar el usuario con sus horarios
            UsuarioHorarioDTO usuarioHorarioDTO = GeneralHorarioSQL.findUsuarioConHorariosById(connection, id);
            if (usuarioHorarioDTO == null) {
                return "Error... usuario no encontrado.";
            }

            if (!usuarioHorarioDTO.rol.equalsIgnoreCase("barbero")) {
                return "Error... el usuario asignado no es un barbero.";
            }

            if (usuarioHorarioDTO.getHorarios().isEmpty()) {
                return "El barbero " + usuarioHorarioDTO.nombre + " " + usuarioHorarioDTO.apellido +
                        " aún no tiene horarios asignados.";
            }
            return renderizarHorarios(usuarioHorarioDTO);

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }


    private String renderizarHorarios(UsuarioHorarioDTO usuarioHorarioDTO) {
        StringBuilder result = new StringBuilder();

        result.append("=============== HORARIO DE BARBERO ===============\r\n")
                .append("ID: ").append(usuarioHorarioDTO.id).append("\r\n")
                .append("Nombre: ").append(usuarioHorarioDTO.nombre).append(" ").append(usuarioHorarioDTO.apellido).append("\r\n")
                .append("Email: ").append(usuarioHorarioDTO.email).append("\r\n")
                .append("--------------------------------------------------\r\n");

        for (HorarioDTO horario : usuarioHorarioDTO.getHorarios()) {
            result.append(String.format(
                    "Día: %-10s | Inicio: %-5s | Fin: %-5s\r\n",
                    horario.dia, horario.horaInicio, horario.horaFin
            ));
        }

        result.append("==================================================\r\n");
        return result.toString();
    }
}
