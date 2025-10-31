package Database;

import Utils.SQLUtils;
import Utils.SocketUtils;

import java.sql.*;

//tecno web
//psql -U agenda -d db_agenda -h mail.tecnoweb.org.bo -c 'SELECT * FROM persona'
//password = agendaagenda
//bd = db_agenda
//table persona
// import java.sql.*
//conexion con BD psql -U agenda -d db_agenda -h mail.tecnoweb.org.bo -c 'SELECT * FROM persona'
public class PGSQLClient {
    private String server;
    private String user;
    private String password;
    private String bdName;
    //private String bdTable;

    public PGSQLClient(String server,String user,String password, String bdName){
        this.server = server;
        this.user = user;
        this.password = password;
        this.bdName = bdName;
        //this.bdTable = bdTable;
    }
    public PGSQLClient(){
        this.server = SocketUtils.MAIL_SERVER;
        this.user = SQLUtils.DB_USER;
        this.password = SQLUtils.DB_PASSWORD;
        this.bdName = SQLUtils.DB_NAME;
        //this.bdTable = SQLUtils.DB_TABLE;
    }

    public void executePGSQLClientForPersonTable(String query){
        String databaseUrl = "jdbc:postgresql://" + this.getServer() + ":5432/" + this.getBdName();
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


    public String executePGSQLClientForPersonTableForPatronQuery(String query){
        String databaseUrl = "jdbc:postgresql://" + this.getServer() + ":5432/" + this.getBdName();
        String tuplas = "";
        try{
            Connection connection = DriverManager.getConnection(databaseUrl,this.getUser(),this.getPassword());
            System.out.println("Connecting successfully to database");
            //Consultas
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            //printResultForQueryTablePerson(resultSet);
            tuplas = generarRespuestaParaCorreo(resultSet);
        }catch(Exception e){
            System.out.println("Throw: " + e.getMessage());
        }
        return tuplas;
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
    public static String generarRespuestaParaCorreo(ResultSet resultSet) throws  SQLException{
        String acronimo = "per_";
        String result = "";
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
            result += String.format(
                    "========================RESULT - CODIGO [%s]========================\r\n" +
                            "codigo: %s\r\n" +
                            "nombre: %s\r\n" +
                            "apellido: %s\r\n" +
                            "profesion: %s\r\n" +
                            "telefono: %s\r\n" +
                            "celular: %s\r\n" +
                            "email: %s\r\n" +
                            "direccion: %s\r\n" +
                            "lugar nacimiento: %s\r\n" +
                            "foto: %s\r\n" +
                    "=====================================================================\r\n"
                    ,
                    codigo,codigo, nombre, apellido, profesion, telefono, celular, email, direccion, lugarNacimiento, foto
            );
        }
        result += ".\r\n";
        resultSet.close();
        return result;
    }



    public static void main(String[] args) {
        PGSQLClient pgsqlClient = new PGSQLClient();
        pgsqlClient.executePGSQLClientForPersonTable("SELECT * FROM persona");
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBdName() {
        return bdName;
    }

    public void setBdName(String bdName) {
        this.bdName = bdName;
    }

//    public String getBdTable() {
//        return bdTable;
//    }
//
//    public void setBdTable(String bdTable) {
//        this.bdTable = bdTable;
//    }


}
