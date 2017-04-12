import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by mdaigle on 4/11/17.
 */
public class DbManager {
    private Connection conn;

    private static final String SQL_CLASS = "org.sqlite.JDBC";
    private static final String CONNECTION_PATH = "jdbc:sqlite:test.db";

    public DbManager() {
        try {
            Class.forName(SQL_CLASS);
            conn = DriverManager.getConnection(CONNECTION_PATH);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}
