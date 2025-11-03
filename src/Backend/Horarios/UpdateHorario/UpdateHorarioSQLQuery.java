package Backend.Horarios.UpdateHorario;

import Backend.Horarios.dto.HorarioUpdateDTO;
import Database.PGSQLClient;

import java.sql.*;
import java.time.LocalTime;

public class UpdateHorarioSQLQuery {

    private static final String SQL_EXISTE_HORARIO = """
        SELECT COUNT(*) AS cantidad
        FROM public.horario_barberos
        WHERE id = ? AND usuario_id = ?
        """;

    private static final String SQL_UPDATE_HORARIO = """
        UPDATE public.horario_barberos
        SET hora_inicio = ?, hora_fin = ?
        WHERE id = ? AND usuario_id = ?
        """;

    public String executeUpdateHorarioQuery(PGSQLClient pgsqlClient, HorarioUpdateDTO horarioDTO) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Conectado correctamente a la base de datos.");
            boolean existe = false;
            try (PreparedStatement ps = connection.prepareStatement(SQL_EXISTE_HORARIO)) {
                ps.setLong(1, horarioDTO.horarioId);
                ps.setLong(2, horarioDTO.id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        existe = rs.getInt("cantidad") > 0;
                    }
                }
            }

            if (!existe) {
                return "Error: No existe un horario con ID " + horarioDTO.horarioId +
                        " asignado al barbero con ID " + horarioDTO.id + ".";
            }

            LocalTime inicio = LocalTime.parse(horarioDTO.horaInicio);
            LocalTime fin = LocalTime.parse(horarioDTO.horaFin);
            int filasAfectadas = 0;
            try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE_HORARIO)) {
                ps.setTime(1, Time.valueOf(inicio));
                ps.setTime(2, Time.valueOf(fin));
                ps.setLong(3, horarioDTO.horarioId);
                ps.setLong(4, horarioDTO.id);

                filasAfectadas = ps.executeUpdate();
            }

            if (filasAfectadas == 0) {
                return "Error: no se actualizó el horario. Verifica los datos enviados.";
            }

            return "Horario actualizado correctamente.\r\n" +
                    "Barbero ID: " + horarioDTO.id + "\r\n" +
                    "Horario ID: " + horarioDTO.horarioId + "\r\n" +
                    "día: " + horarioDTO.dia + "\r\n" +
                    "Nuevo horario: " + horarioDTO.horaInicio + " a " + horarioDTO.horaFin;

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}
