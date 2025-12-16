package Backend.Horarios.CrearHorario;

import Backend.Horarios.GeneralHorarioSQL;
import Backend.Horarios.dto.HorarioDTO;
import Backend.Horarios.dto.UsuarioHorarioDTO;
import Backend.Roles;
import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.time.LocalTime;

public class CreateHorarioSQLQuery {
    private static final String INSERT_HORARIOS = """
            INSERT INTO horario_barberos (barbero_id, dia_semana, hora_inicio, hora_fin)
            VALUES (?, ?, ?, ?)
            """;
    public String executeInsertHorarioQuery(PGSQLClient pgsqlClient, HorarioDTO dto) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");
            UsuarioHorarioDTO usuarioHorarioDTO = GeneralHorarioSQL.findUsuarioConHorariosById(connection, dto.id);
            if(usuarioHorarioDTO == null){
                return "Error... usuario no encontrado.";
            }
            if(!usuarioHorarioDTO.rol.equalsIgnoreCase(Roles.BARBERO.getDescripcion())){
                return "Error... el usuario asignado no es un barbero";
            }
            boolean existeBarbero = GeneralUsuarioSQLUtils.existeUsuarioConRol(connection, usuarioHorarioDTO.id, Roles.BARBERO.getDescripcion());
            if(!existeBarbero){
                return "Error... el usuario no fue encontrado en la tabla barbero..";
            }

            boolean existeElDiaEnElHorarioAsignado = GeneralHorarioSQL.existeElDiaEnElHorarioAsignado(usuarioHorarioDTO, dto.dia);
            if (existeElDiaEnElHorarioAsignado) {
                return "Error...el dia ya tiene un horario asignado";
            }
            LocalTime inicio = LocalTime.parse(dto.horaInicio);
            LocalTime fin = LocalTime.parse(dto.horaFin);
            try (PreparedStatement ps = connection.prepareStatement(INSERT_HORARIOS)) {
                ps.setLong(1, dto.id);
                ps.setString(2, dto.dia.toLowerCase());
                ps.setTime(3, Time.valueOf(inicio));
                ps.setTime(4, Time.valueOf(fin));

                int filas = ps.executeUpdate();
                if (filas == 0) {
                    return "Error: no se pudo insertar el horario.";
                }
            }

            return "Horario insertado con éxito para el usuario ID " + dto.id +
                    " (" + dto.dia + " de " + dto.horaInicio + " a " + dto.horaFin + ").";

        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}
