package Backend.Horarios.CrearHorario;

import Backend.Horarios.GeneralHorarioSQL;
import Backend.Horarios.dto.HorarioDTO;
import Backend.Horarios.dto.UsuarioHorarioDTO;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.time.LocalTime;

public class CreateHorarioSQLQuery {
    private static final String INSERT_HORARIOS = """
            INSERT INTO horario_barberos (usuario_id, dia_semana, hora_inicio, hora_fin)
            VALUES (?, ?, ?, ?)
            """;
    public String executeInsertHorarioQuery(PGSQLClient pgsqlClient, HorarioDTO horarioDTO) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");
            UsuarioHorarioDTO usuarioHorarioDTO = GeneralHorarioSQL.findUsuarioConHorariosById(connection,horarioDTO.id);
            if(usuarioHorarioDTO == null){
                return "Error... usuario no encontrado.";
            }
            if(!usuarioHorarioDTO.rol.equalsIgnoreCase("barbero")){
                return "Error... el usuario asignado no es un barbero";
            }
            boolean existeElDiaEnElHorarioAsignado = GeneralHorarioSQL.existeElDiaEnElHorarioAsignado(usuarioHorarioDTO,horarioDTO.dia);
            if (existeElDiaEnElHorarioAsignado) {
                return "Error...el dia ya tiene un horario asignado";
            }
            LocalTime inicio = LocalTime.parse(horarioDTO.horaInicio);
            LocalTime fin = LocalTime.parse(horarioDTO.horaFin);
            try (PreparedStatement ps = connection.prepareStatement(INSERT_HORARIOS)) {
                ps.setLong(1, horarioDTO.id);
                ps.setString(2, horarioDTO.dia.toLowerCase());
                ps.setTime(3, Time.valueOf(inicio));
                ps.setTime(4, Time.valueOf(fin));

                int filas = ps.executeUpdate();
                if (filas == 0) {
                    return "Error: no se pudo insertar el horario.";
                }
            }

            return "Horario insertado con éxito para el usuario ID " + horarioDTO.id +
                    " (" + horarioDTO.dia + " de " + horarioDTO.horaInicio + " a " + horarioDTO.horaFin + ").";

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}
