package Backend.Usuarios;

import Backend.Roles;
import Backend.Servicio.dto.UpdateServicioDTO;
import Backend.Usuarios.dto.BarberoServicioDTO;
import Backend.Usuarios.dto.UpdateUsuarioDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;

public class GeneralUsuarioSQLUtils {
    private static final String SQL_EXISTS =
            "SELECT EXISTS(SELECT 1 FROM users WHERE id = ?)";
    private static final String SQL_EXISTS_CLIENTE =
            "SELECT EXISTS(SELECT 1 FROM clientes WHERE id = ?)";
    private static final String SQL_EXISTS_BARBERO =
            "SELECT EXISTS(SELECT 1 FROM barberos WHERE id = ?)";
    private static  final String SQL_EXISTS_EMAIL = "SELECT 1 FROM users WHERE email = ?";

    private static final String[] ROLES_PERMITIDOS = {
            "barbero", "propietario", "secretaria", "cliente"
    };

    //solo usado por roles de barbero o cliente,administrador o otro rol qu eno tenga una tabla independiente no creara nada
    public static boolean existeUsuarioConRol(Connection con, long id,String rol) throws SQLException {
        String query;
        if(rol.equalsIgnoreCase(Roles.BARBERO.getDescripcion())){
            query = SQL_EXISTS_BARBERO;
        }else if(rol.equalsIgnoreCase(Roles.CLIENTE.getDescripcion())){
            query = SQL_EXISTS_CLIENTE;
        }else {
            query = SQL_EXISTS;
        }
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static UpdateUsuarioDTO findUserById(Connection con, long id) throws SQLException {
        String SQL_FIND = "SELECT id, nombre, apellido, email, telefono, password, rol, estado,deleted_at FROM users WHERE id = ?";

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
                    usuario.estado = rs.getString("estado");
                    usuario.deletedAt = rs.getString("deleted_at");
                    return usuario;
                }
                return null;
            }
        }
    }

    public static BarberoServicioDTO findBarberoConServiciosById(Connection con, long barberoId) throws SQLException {
        String sql = """
        SELECT u.id, u.name, u.apellido, u.email, u.telefono, b.estado_barbero AS estado
               s.id AS s_id, s.nombre AS s_nombre, s.descripcion, 
               s.precio, s.duracion_estimada, s.estado AS s_estado
        FROM users u
        JOIN barberos b ON u.id = b.id
        JOIN servicio_barberos sb ON b.id = sb.barbero_id
        JOIN servicios s ON sb.servicio_id = s.id
        WHERE u.id = ?;
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, barberoId);

            try (ResultSet rs = ps.executeQuery()) {
                BarberoServicioDTO barberoDTO = null;

                while (rs.next()) {
                    // Solo inicializamos el DTO en la primera fila encontrada
                    if (barberoDTO == null) {
                        barberoDTO = new BarberoServicioDTO();
                        barberoDTO.id = rs.getLong("id");
                        barberoDTO.nombre = rs.getString("name");
                        barberoDTO.apellido = rs.getString("apellido");
                        barberoDTO.email = rs.getString("email");
                        barberoDTO.telefono = rs.getString("telefono");
                        barberoDTO.estado = rs.getString("estado");
                        barberoDTO.servicios = new HashSet<>();
                    }

                    // Agregamos cada servicio al Set del barbero
                    UpdateServicioDTO servicio = new UpdateServicioDTO();
                    servicio.id = rs.getLong("s_id");
                    servicio.nombre = rs.getString("s_nombre");
                    servicio.descripcion = rs.getString("descripcion");
                    servicio.precio = rs.getFloat("precio");
                    servicio.duracion = rs.getInt("duracion_estimada");
                    servicio.estado = rs.getString("s_estado");
                    servicio.deletedAt = null;

                    barberoDTO.servicios.add(servicio);
                }

                return barberoDTO; // Retornará null si el barbero no existe o no tiene servicios
            }
        }
    }

    public static UpdateUsuarioDTO findUserByEmail(Connection con, String email) throws SQLException {
        String SQL_FIND = "SELECT id, nombre, apellido, email, telefono, password, rol, estado, deleted_at FROM users WHERE email = ?";

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
                    usuario.estado = rs.getString("estado");
                    usuario.deletedAt = rs.getString("deleted_at");
                    return usuario;
                }
                return null; // No encontrado
            }
        }
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
