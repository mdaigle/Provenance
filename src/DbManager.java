import sun.tools.jconsole.Tab;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by mdaigle on 4/11/17.
 */
public class DbManager {
    private static final String SQL_CLASS = "org.sqlite.JDBC";
    public static final String SYSTEM_DB_CONNECTION_STRING = "jdbc:sqlite:system.db";
    public static final String DATA_DB_CONNECTON_STRING = "jdbc:sqlite:data.db";

    /**
     * SQL statement to create the tools table.
     */
    private static final String CREATE_TOOLS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS Tools" +
            "(ID                INTEGER     PRIMARY KEY, " +
            "NAME               TEXT        NOT NULL, " +
            "NUM_INPUT_TABLES   INTEGER     UNSIGNED)";

    /**
     * SQL statement to create a data table.
     */
    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS ?" +
            "(ID                INTEGER     PRIMARY KEY, " +
            "VALUE              INTEGER     UNSIGNED)";

    private static final String ADD_TOOL_SQL = "INSERT INTO Tools VALUES" +
            "(NULL, ?, ?)";

    private static final String GET_TOOLS_SQL = "SELECT * FROM TOOLS";

    private static final String GET_TABLES_SQL = "SELECT name FROM sqlite_master WHERE type='table'";

    /**
     * SQL statement to insert a row into a data table.
     */
    private static final String INSERT_ROW_SQL = "INSERT INTO ? VALUES (?)";

    private String systemConnection;
    private String dataConnection;

    /**
     * Creates a new DbManager and initializes its connection to test.db.
     */
    public DbManager(String systemConnection, String dataConnection) {
        this.systemConnection = systemConnection;
        this.dataConnection = dataConnection;
    }

    /**
     * Ensures that the necessary tables have been created in test.db. Should be called after construction.
     */
    public void initializeDb() {
        Connection conn;
        try {
            // Open a connection to the db
            Class.forName(SQL_CLASS);
            conn = DriverManager.getConnection(systemConnection);

            // Create Tools table
            Statement s = conn.createStatement();
            s.executeUpdate(CREATE_TOOLS_TABLE_SQL);

            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void createTable(Table table) {
        Connection conn;
        try {
            // Open a connection to the db
            Class.forName(SQL_CLASS);
            conn = DriverManager.getConnection(dataConnection);

            // Create the table
            // (Can't do substitution for table names, so do string replace instead)
            String SQL = CREATE_TABLE_SQL.replace("?", table.getName());
            Statement s = conn.createStatement();
            s.executeUpdate(SQL);

            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void addTool(Tool tool) {
        Connection conn;
        try {
            // Open a connection to the db
            Class.forName(SQL_CLASS);
            conn = DriverManager.getConnection(systemConnection);

            // Create the table
            PreparedStatement s = conn.prepareStatement(ADD_TOOL_SQL);
            s.setString(1, tool.getName());
            s.setInt(2, tool.getNumTables());
            s.executeUpdate();

            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public Collection<Tool> getTools() {
        Connection conn;
        try {
            // Open a connection to the db
            Class.forName(SQL_CLASS);
            conn = DriverManager.getConnection(systemConnection);

            // Create the table
            Statement s = conn.createStatement();
            ResultSet results = s.executeQuery(GET_TOOLS_SQL);

            HashSet<Tool> tools = new HashSet<>();
            while (results.next()) {
                //TODO: zero-indexed?
                String name = results.getString(2);
                int numTables = results.getInt(3);

                Tool tool = new Tool(name, numTables);
                tools.add(tool);
            }

            conn.close();
            return tools;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return null;
    }

    public Collection<Table> getTables() {
        Connection conn;
        try {
            // Open a connection to the db
            Class.forName(SQL_CLASS);
            conn = DriverManager.getConnection(dataConnection);

            // Create the table
            Statement s = conn.createStatement();
            ResultSet results = s.executeQuery(GET_TABLES_SQL);

            HashSet<Table> tables = new HashSet<>();
            while (results.next()) {
                //TODO: zero-indexed?
                String name = results.getString(1);

                Table table = new Table(name);
                tables.add(table);
            }

            conn.close();
            return tables;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return null;
    }

    public void addRow(Table table, int rowValue) {
        Connection conn;
        try {
            // Open a connection to the db
            Class.forName(SQL_CLASS);
            conn = DriverManager.getConnection(dataConnection);

            // Create the table
            /*PreparedStatement s = conn.prepareStatement();
            s.setString(1, table.getName());
            s.executeUpdate();*/

            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}
