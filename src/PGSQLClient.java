import java.sql.*;

//tecno web
//psql -U agenda -d db_agenda -h mail.tecnoweb.org.bo -c 'SELECT * FROM persona'
//password = agendaagenda
//bd = db_agenda
//table persona
// import java.sql.*

public class PGSQLClient {
    private String server;
    private String user;
    private String password;
    private String bdName;
    private String bdTable;
    public PGSQLClient(){
        this.server = SocketUtils.MAIL_SERVER;
        this.user = SocketUtils.DB_USER;
        this.password = SocketUtils.DB_PASSWORD;
        this.bdName = SocketUtils.DB_NAME;
        this.bdTable = SocketUtils.DB_TABLE;
    }
    public void executePGSQLClientForPersonTable(String query){
        String databaseUrl = "jdbc:postgresql://" + this.server + ":5432/" + this.bdName;
        try{
            Connection connection = DriverManager.getConnection(databaseUrl,this.user,this.password);
            System.out.println("Connecting successfully to database");
            //Consultas
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            printResultForQueryTablePerson(resultSet);

        }catch(Exception e){
            System.out.println("Throw: " + e.getMessage());
        }
    }
    public static void printResultForQueryTablePerson(ResultSet resultSet) throws SQLException {

        String acronimo = "per_";
        while(resultSet.next()) {
            String codigo = resultSet.getString(acronimo + "cod");
            String nombre = resultSet.getString(acronimo + "nom");
            String apellido = resultSet.getString(acronimo + "appm");
            String profesion = resultSet.getString(acronimo + "prof");
            String telefono = resultSet.getString(acronimo + "telf");
            String celular = resultSet.getString(acronimo + "cel");
            String email = resultSet.getString(acronimo + "email");
            String direccion = resultSet.getString(acronimo + "dir");
            String lugarNacimiento = resultSet.getString(acronimo + "lnac");
            String foto = resultSet.getString(acronimo + "foto");
            System.out.println("==================================== Persona -" + codigo + "====================================");
            System.out.println(
                    "codigo: " + codigo +
                    "\nnombre: " + nombre +
                    "\napellido: " + apellido +
                    "\nprofesion: " + profesion +
                    "\ntelefono: " + telefono +
                    "\ncelular: " + celular +
                    "\nemail: " + email +
                    "\ndireccion: " + direccion +
                    "\nlugar nacimiento: " + lugarNacimiento +
                    "\nfoto: " + foto
            );
        }
        resultSet.close();
    }
    public static void main(String[] args) {
        PGSQLClient pgsqlClient = new PGSQLClient();
        pgsqlClient.executePGSQLClientForPersonTable("SELECT * FROM persona");
    }


}
