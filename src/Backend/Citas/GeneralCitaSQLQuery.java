package Backend.Citas;

import Backend.Citas.dto.CitaDTO;
import Backend.Servicio.dto.UpdateServicioDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class GeneralCitaSQLQuery {
    private static final String SQL_FIND_CITA_BY_ID =
            "SELECT id, fecha, estado, pago_inicial, monto_total, porcentaje_cita, barbero_id, cliente_id " +
                    "FROM citas WHERE id = ?";

    private static final String SQL_FIND_SERVICIOS_BY_CITA =
            "SELECT s.id, s.nombre, s.precio FROM servicios s " +
                    "JOIN cita_servicios cs ON s.id = cs.servicio_id " +
                    "WHERE cs.cita_id = ?";

    public static CitaDTO findCitaById(Connection connection, Long citaId) throws SQLException {
        CitaDTO cita = null;

        // 1. Obtener datos generales de la cita
        try (PreparedStatement psCita = connection.prepareStatement(SQL_FIND_CITA_BY_ID)) {
            psCita.setLong(1, citaId);
            try (ResultSet rsCita = psCita.executeQuery()) {
                if (rsCita.next()) {
                    cita = new CitaDTO();
                    cita.id = rsCita.getLong("id");
                    // Convertimos el Timestamp de BD a String para el DTO
                    cita.fecha = rsCita.getTimestamp("fecha").toString();
                    cita.estado = rsCita.getString("estado");
                    cita.pagoInicial = rsCita.getDouble("pago_inicial");
                    cita.montoTotal = rsCita.getDouble("monto_total");
                    cita.porcentajeCita = rsCita.getDouble("porcentaje_cita");
                    cita.barberoId = rsCita.getLong("barbero_id");
                    cita.clienteId = rsCita.getLong("cliente_id");
                    cita.servicios = new HashSet<>();
                }
            }
        }

        // 2. Si la cita existe, cargar sus servicios asociados
        if (cita != null) {
            // SQL ampliado para traer toda la info del servicio
            String sqlServicios = """
            SELECT s.id, s.nombre, s.descripcion, s.precio, s.duracion_estimada as duracion, s.estado, s.deleted_at 
            FROM servicios s 
            JOIN cita_servicios cs ON s.id = cs.servicio_id 
            WHERE cs.cita_id = ?
        """;

            try (PreparedStatement psServs = connection.prepareStatement(sqlServicios)) {
                psServs.setLong(1, citaId);
                try (ResultSet rsServs = psServs.executeQuery()) {
                    while (rsServs.next()) {
                        UpdateServicioDTO serv = new UpdateServicioDTO();
                        serv.id = rsServs.getLong("id");
                        serv.nombre = rsServs.getString("nombre");
                        serv.descripcion = rsServs.getString("descripcion");
                        serv.precio = rsServs.getFloat("precio");
                        serv.duracion = rsServs.getInt("duracion");
                        serv.estado = rsServs.getString("estado");
                        serv.deletedAt = rsServs.getString("deleted_at");
                        cita.servicios.add(serv);
                    }
                }
            }
        }
        return cita;
    }
}
