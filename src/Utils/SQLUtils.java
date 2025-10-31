package Utils;

public class SQLUtils {
    public static String DB_USER = "agenda";
    public static String DB_PASSWORD = "agendaagenda";
    public static String DB_NAME = "db_agenda";
    public static String DB_TABLE = "persona";

    public static String DB_GRUPO_USER = "grupo14sc";
    public static String DB_GRUPO_PASSWORD = "grup014grup014*";
    public static String DB_GRUPO_DB_NAME = "db_grupo14sc";
    //public static String DB_USUARIO_TABLE = "usuario";
    public static String getQueryForPatron(String substring) {
        return String.format("""
        SELECT *
        FROM persona
        WHERE per_nom LIKE '%%%s%%' OR per_nom LIKE '%%%s%%'
        """, substring, substring.toUpperCase());
    }

}
