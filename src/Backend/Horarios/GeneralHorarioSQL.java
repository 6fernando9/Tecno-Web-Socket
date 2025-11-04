package Backend.Horarios;

import Backend.Horarios.dto.HorarioDTO;
import Backend.Horarios.dto.UsuarioHorarioDTO;
import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Usuarios.dto.UpdateUsuarioDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class GeneralHorarioSQL {
    private static final String[] DIAS_DE_LA_SEMANA= {
            "lunes","martes","miercoles","jueves","viernes","sabado","domingo"
    };
    public static boolean esDiaValido(String cadena){
        return Arrays.asList(DIAS_DE_LA_SEMANA).contains(cadena.toLowerCase());
    }

    public static UsuarioHorarioDTO findUsuarioConHorariosById(Connection connection, long id) throws SQLException {
        String SQL_HORARIOS = """
                SELECT id, dia_semana, hora_inicio, hora_fin
                FROM horario_barberos WHERE usuario_id = ?
                ORDER BY id ASC
                """;
        UpdateUsuarioDTO usuarioDTO = GeneralUsuarioSQLUtils.findUserById(connection,id);
        if(usuarioDTO == null){
            return null;
        }
        //public UsuarioHorarioDTO(Long id, String nombre, String apellido, String email, String telefono, String password, String rol) {
        UsuarioHorarioDTO usuarioHorarioDTO = new UsuarioHorarioDTO(
                usuarioDTO.id,
                usuarioDTO.nombre,
                usuarioDTO.apellido,
                usuarioDTO.email,
                usuarioDTO.telefono,
                usuarioDTO.password,
                usuarioDTO.rol,null,null);

        try (PreparedStatement psHorarios = connection.prepareStatement(SQL_HORARIOS)) {
            psHorarios.setLong(1, id);
            try (ResultSet rsHorarios = psHorarios.executeQuery()) {
                while (rsHorarios.next()) {
                    HorarioDTO horario = new HorarioDTO(
                            rsHorarios.getLong("id"),
                            rsHorarios.getString("dia_semana"),
                            rsHorarios.getString("hora_inicio"),
                            rsHorarios.getString("hora_fin")
                    );
                    usuarioHorarioDTO.getHorarios().add(horario);
                }
            }
        }
        return usuarioHorarioDTO;
    }
    public static boolean existeElDiaEnElHorarioAsignado(UsuarioHorarioDTO usuarioHorarioDTO, String dia) {
        if (usuarioHorarioDTO == null || usuarioHorarioDTO.getHorarios() == null) {
            return false;
        }
        return usuarioHorarioDTO.getHorarios().stream()
                .anyMatch(h -> h.dia != null && h.dia.equalsIgnoreCase(dia));//ignore equals pa evaluar sin importar mayus o minus
    }
}
