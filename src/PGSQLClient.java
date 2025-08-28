//tecno web
//psql -U agenda -d db_agenda -h mail.tecnoweb.org.bo -c 'SELECT * FROM persona'
//password = agendaagenda
//bd = db_agenda
//table persona
// import java.sql.*
public class PGSQLClient {
    String server;
    String user;
    String password;
    String bdName;
    String bdTable;
    public PGSQLClient(){
        this.server = SocketUtils.MAIL_SERVER;
        this.user = SocketUtils.DB_USER;
        this.password = SocketUtils.DB_PASSWORD;
        this.bdName = SocketUtils.DB_NAME;
        this.bdTable = SocketUtils.DB_TABLE;
    }



}
