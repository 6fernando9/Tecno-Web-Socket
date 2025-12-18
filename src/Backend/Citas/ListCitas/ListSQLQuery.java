package Backend.Citas.ListCitas;

import Backend.Citas.dto.ListarCitaDTO;
import Database.PGSQLClient;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ListSQLQuery {

    public String listCitas(PGSQLClient pgsqlClient, ListarCitaDTO dto) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        // Base de la consulta con JOIN para traer servicios en una sola fila
        StringBuilder sql = new StringBuilder(
                "SELECT c.id, c.cliente_id, c.barbero_id, c.fecha, c.estado, c.pago_inicial, " +
                        "STRING_AGG(cs.servicio_id::text, ', ') AS servicios_ids " +
                        "FROM citas c " +
                        "LEFT JOIN cita_servicios cs ON cs.cita_id = c.id " +
                        "WHERE 1=1"
        );

        // 1. Construcción Dinámica
        if (dto.clienteId != null)  sql.append(" AND c.cliente_id = ?");
        if (dto.barberoId != null)  sql.append(" AND c.barbero_id = ?");
        if (dto.estado != null)     sql.append(" AND c.estado = ?");
        if (dto.fechaInicio != null) sql.append(" AND c.fecha >= ?");
        if (dto.fechaFin != null)    sql.append(" AND c.fecha <= ?");
        if (dto.servicioId != null)  sql.append(" AND cs.servicio_id = ?");

        sql.append(" GROUP BY c.id, c.cliente_id, c.barbero_id, c.fecha, c.estado, c.pago_inicial ");
        sql.append(" ORDER BY c.fecha DESC");


        StringBuilder response = new StringBuilder("📋 LISTADO DE CITAS FILTRADO:\r\n");
        DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        try (Connection con = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {

                // 2. Seteo Dinámico de Parámetros
                int i = 1;
                if (dto.clienteId != null)  ps.setLong(i++, dto.clienteId);
                if (dto.barberoId != null)  ps.setLong(i++, dto.barberoId);
                if (dto.estado != null)     ps.setString(i++, dto.estado);
                if (dto.fechaInicio != null) ps.setTimestamp(i++, Timestamp.valueOf(dto.fechaInicio));
                if (dto.fechaFin != null)    ps.setTimestamp(i++, Timestamp.valueOf(dto.fechaFin));
                if (dto.servicioId != null)  ps.setLong(i++, dto.servicioId);

                try (ResultSet rs = ps.executeQuery()) {
                    boolean hayDatos = false;
                    while (rs.next()) {
                        hayDatos = true;
                        String linea = String.format(
                                "ID: %d | Cli: %d | Barb: %d | Fecha: %s | Est: %s | Pago: %.2f | Servs: [%s]",
                                rs.getLong("id"),
                                rs.getLong("cliente_id"),
                                rs.getLong("barbero_id"),
                                rs.getTimestamp("fecha").toLocalDateTime().format(formatoSalida),
                                rs.getString("estado").toUpperCase(),
                                rs.getDouble("pago_inicial"),
                                rs.getString("servicios_ids")
                        );
                        response.append(linea).append("\r\n");
                    }

                    if (!hayDatos) return "No se encontraron citas con esos filtros.";
                }
            }
            return response.toString();

        } catch (SQLException e) {
            return "ERROR DB: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }


}
