package Backend.Horarios.DeleteHorario;

import Database.PGSQLClient;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DeleteHorarioSQLQuery {

    private static final String SQL_EXISTE_HORARIO = """
        SELECT COUNT(*) AS cantidad
        FROM public.horario_barberos
        WHERE id = ? AND usuario_id = ?
        """;

    private static final String SQL_DELETE_HORARIO = """
        DELETE FROM public.horario_barberos
        WHERE id = ? AND usuario_id = ?
        """;

    public String executeEliminarHorario(PGSQLClient pgsqlClient, long barberoId, long horarioId) {
        String databaseUrl = "jdbc:postgresql://" + pgsqlClient.getServer() + ":5432/" + pgsqlClient.getBdName();

        try (Connection connection = DriverManager.getConnection(
                databaseUrl, pgsqlClient.getUser(), pgsqlClient.getPassword())) {

            System.out.println("Conectado correctamente a la base de datos");
            boolean existe = false;
            try (PreparedStatement ps = connection.prepareStatement(SQL_EXISTE_HORARIO)) {
                ps.setLong(1, horarioId);
                ps.setLong(2, barberoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        existe = rs.getInt("cantidad") > 0;
                    }
                }
            }
            if (!existe) {
                return "No existe un horario con ID " + horarioId + " asignado al barbero " + barberoId + ".";
            }

            int filasEliminadas = 0;
            try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE_HORARIO)) {
                ps.setLong(1, horarioId);
                ps.setLong(2, barberoId);
                filasEliminadas = ps.executeUpdate();
            }

            if (filasEliminadas == 0) {
                return "No se eliminó ningún registro. Verifica los IDs.";
            }

            return "Horario eliminado correctamente para el barbero con ID " + barberoId +
                    ". ID de horario eliminado: " + horarioId + ".";
        } catch (Exception e) {
            System.out.println("Throw: " + e.getMessage());
            return "ERROR DE BASE DE DATOS: " + e.getMessage();
        }
    }
}
