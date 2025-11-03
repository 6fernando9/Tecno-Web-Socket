package Backend.Servicio;

import Backend.Servicio.dto.UpdateServicioDTO;
import Backend.Usuarios.dto.UpdateUsuarioDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GeneralServicioSQLUtils {
    public static UpdateServicioDTO findServicioById(Connection con, long id) throws SQLException {
        String SQL_FIND = "SELECT id, nombre, descripcion, precio, duracion_estimada FROM servicios WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(SQL_FIND)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UpdateServicioDTO updateServicioDTO = new UpdateServicioDTO();
                    updateServicioDTO.id = rs.getLong("id");
                    updateServicioDTO.nombre = rs.getString("nombre");
                    updateServicioDTO.descripcion = rs.getString("descripcion");
                    updateServicioDTO.precio = rs.getFloat("precio");
                    updateServicioDTO.duracion = rs.getInt("duracion_estimada");
                    return updateServicioDTO;
                }
                return null;
            }
        }
    }
}
