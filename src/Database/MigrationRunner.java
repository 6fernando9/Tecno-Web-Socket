package Database;

import Utils.SQLUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MigrationRunner {

    public static void runSqlFile(String filePath) throws IOException {
        String sql = Files.readString(Path.of(filePath));
        // Split statements by semicolon. This is a simple splitter and may fail with
        // procedure bodies that contain semicolons. For our migration it's acceptable.
        String[] statements = sql.split(";\n");

        String databaseUrl = "jdbc:postgresql://" + SQLUtils.DB_GRUPO_USER + 
                ":5432/" + SQLUtils.DB_GRUPO_DB_NAME; // fallback: use group constants if present

        // Prefer default DB creds if set
        String dbUser = SQLUtils.DB_USER != null ? SQLUtils.DB_USER : SQLUtils.DB_GRUPO_USER;
        String dbPassword = SQLUtils.DB_PASSWORD != null ? SQLUtils.DB_PASSWORD : SQLUtils.DB_GRUPO_PASSWORD;
        String dbName = SQLUtils.DB_NAME != null ? SQLUtils.DB_NAME : SQLUtils.DB_GRUPO_DB_NAME;
        databaseUrl = "jdbc:postgresql://" + "localhost" + ":5432/" + dbName; // default host localhost

        try (Connection conn = DriverManager.getConnection(databaseUrl, dbUser, dbPassword)) {
            conn.setAutoCommit(false);
            try (Statement st = conn.createStatement()) {
                for (String stmt : statements) {
                    String s = stmt.trim();
                    if (s.isEmpty()) continue;
                    // Some statements may have comments at start; remove leading -- lines
                    // We'll execute the statement as-is
                    System.out.println("Executing statement: " + (s.length() > 80 ? s.substring(0, 80) + "..." : s));
                    st.execute(s);
                }
                conn.commit();
                System.out.println("Migration executed successfully.");
            } catch (Exception e) {
                conn.rollback();
                System.out.println("Migration failed: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception ex) {
            System.out.println("Unable to connect or run migration: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String path = "src/Database/migrations/create_barberia.sql";
        if (args.length > 0) path = args[0];
        try {
            runSqlFile(path);
        } catch (IOException e) {
            System.out.println("Error reading SQL file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
