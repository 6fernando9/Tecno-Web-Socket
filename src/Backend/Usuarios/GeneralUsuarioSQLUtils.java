package Backend.Usuarios;

import Backend.Usuarios.dto.UpdateUsuarioDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class GeneralUsuarioSQLUtils {
    private static final String SQL_EXISTS =
            "SELECT EXISTS(SELECT 1 FROM usuarios WHERE id = ?)";
    private static  final String SQL_EXISTS_EMAIL = "SELECT 1 FROM usuarios WHERE email = ?";

    private static final String[] ROLES_PERMITIDOS = {
            "barbero", "propietario", "secretaria", "cliente"
    };

    public static boolean existsUser(Connection con, long id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_EXISTS)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static UpdateUsuarioDTO findUserById(Connection con, long id) throws SQLException {
        String SQL_FIND = "SELECT id, nombre, apellido, email, telefono, password, rol FROM usuarios WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(SQL_FIND)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UpdateUsuarioDTO usuario = new UpdateUsuarioDTO();
                    usuario.id = rs.getLong("id");
                    usuario.nombre = rs.getString("nombre");
                    usuario.apellido = rs.getString("apellido");
                    usuario.email = rs.getString("email");
                    usuario.telefono = rs.getString("telefono");
                    usuario.password = rs.getString("password");
                    usuario.rol = rs.getString("rol");
                    return usuario;
                }
                return null;
            }
        }
    }
    public static UpdateUsuarioDTO findUserByEmail(Connection con, String email) throws SQLException {
        String SQL_FIND = "SELECT id, nombre, apellido, email, telefono, password, rol FROM usuarios WHERE email = ?";

        try (PreparedStatement ps = con.prepareStatement(SQL_FIND)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UpdateUsuarioDTO usuario = new UpdateUsuarioDTO();
                    usuario.id = rs.getLong("id");
                    usuario.nombre = rs.getString("nombre");
                    usuario.apellido = rs.getString("apellido");
                    usuario.email = rs.getString("email");
                    usuario.telefono = rs.getString("telefono");
                    usuario.password = rs.getString("password");
                    usuario.rol = rs.getString("rol");
                    return usuario;
                }
                return null; // No encontrado
            }
        }
    }

    public static String parsearSubjectComillaTriple(String subject){
        subject = subject.trim();
        return subject.replace("\r", "").replace("\n", " ");
    }

    public static boolean esRolPermitido(String rol) {
        return Arrays.asList(ROLES_PERMITIDOS).contains(rol.toLowerCase());
    }

    public static boolean existeUsuarioPorEmail(Connection connection, String email) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_EXISTS_EMAIL)) { // try() , para cerrar la sesion automatico
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


}
