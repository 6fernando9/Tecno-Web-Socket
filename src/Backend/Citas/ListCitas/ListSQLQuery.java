package Backend.Citas.ListCitas;

import Database.PGSQLClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ListSQLQuery {

    public String listCitas(PGSQLClient pgsqlClient, Long clienteId, Long barberoId, String estado, String desde, String hasta, Long servicioId, Integer limit) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();
        List<String> rows = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {
            System.out.println("Connecting successfully to database");
            StringBuilder sql = new StringBuilder(
                    "SELECT c.id, c.cliente_id, c.barbero_id, c.fecha, c.estado, c.pago_inicial, " +
                            "STRING_AGG(cs.servicio_id::text, ',') AS servicios " +
                            "FROM citas c " +
                            "LEFT JOIN cita_servicios cs ON cs.cita_id = c.id " +
                            "WHERE 1=1"
            );

            if (clienteId != null) sql.append(" AND c.cliente_id = ?");
            if (barberoId != null) sql.append(" AND c.barbero_id = ?");
            if (estado != null && !estado.isBlank()) sql.append(" AND c.estado = ?");

            if (desde != null && !desde.isBlank()) sql.append(" AND c.fecha >= ?");
            if (hasta != null && !hasta.isBlank()) sql.append(" AND c.fecha <= ?");

            if (servicioId != null) sql.append(" AND cs.servicio_id = ?");
            sql.append(" GROUP BY c.id, c.cliente_id, c.barbero_id, c.fecha, c.estado, c.pago_inicial ");
            sql.append(" ORDER BY c.fecha DESC");

            if (limit != null && limit > 0) sql.append(" LIMIT ").append(limit);

            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                int idx = 1;
                if (clienteId != null) ps.setLong(idx++, clienteId);
                if (barberoId != null) ps.setLong(idx++, barberoId);
                if (estado != null && !estado.isBlank()) ps.setString(idx++, estado);

                if (desde != null && !desde.isBlank())
                    ps.setTimestamp(idx++, parseFecha(desde, false));

                if (hasta != null && !hasta.isBlank())
                    ps.setTimestamp(idx++, parseFecha(hasta, true));

                if (servicioId != null) ps.setLong(idx++, servicioId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long id = rs.getLong("id");
                        long cid = rs.getLong("cliente_id");
                        long bid = rs.getLong("barbero_id");
                        Timestamp fechaCita = rs.getTimestamp("fecha");
                        String est = rs.getString("estado");
                        Double pago = rs.getDouble("pago_inicial");
                        String servs = rs.getString("servicios");

                        rows.add(String.format("%d | Cliente=%d | Barbero=%d | Fecha=%s | Estado=%s | Pago_Inic=%.2f | Servicios=[%s]",
                                id, cid, bid, fechaCita, est, pago, servs));
                    }
                }
            }

            if (rows.isEmpty()) return "No se encontraron citas con los filtros indicados.";

            StringBuilder out = new StringBuilder();
            out.append("📋 CITAS ENCONTRADAS:\r\n");
            for (String r : rows) {
                out.append(r).append("\r\n");
            }
            return out.toString();

        } catch (Exception e) {
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
    private Timestamp parseFecha(String f, boolean isHasta) throws Exception {
        if (f == null || f.isBlank()) return null;

        // Caso 1: Solo fecha (YYYY-MM-DD)
        if (f.matches("\\d{4}-\\d{2}-\\d{2}")) {
            if (isHasta)
                return Timestamp.valueOf(f + " 23:59:59");
            else
                return Timestamp.valueOf(f + " 00:00:00");
        }

        // Caso 2: ISO con T
        if (f.contains("T"))
            f = f.replace("T", " ");

        // Caso 3: Fecha y hora sin segundos
        if (f.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"))
            f = f + ":00";

        return Timestamp.valueOf(f);
    }

}
