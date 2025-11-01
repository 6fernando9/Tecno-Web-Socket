package Backend.Usuarios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GeneralUsuarioSQLUtils {
    private static final String SQL_EXISTS =
            "SELECT EXISTS(SELECT 1 FROM usuario WHERE id = ?)";
    public static boolean existsUser(Connection con, long id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_EXISTS)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
