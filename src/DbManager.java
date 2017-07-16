import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages tools and data tables.
 */
public class DbManager {
    /**
     * SQL statement to create the tools table.
     */
    private static final String CREATE_TOOLS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS Tools" +
            "(ID                INTEGER     PRIMARY KEY," +
            "NAME               TEXT        NOT NULL," +
            "NUM_INPUT_TABLES   INTEGER     UNSIGNED," +
            "NUM_PARAMETERS     INTEGER     UNSIGNED," +
            "PARAMETER_TYPES    TEXT        NOT NULL)";

    /**
     * SQL statement to create a data table.
     */
    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS ?" +
            "(ID                INTEGER     PRIMARY KEY, " +
            "VALUE              INTEGER     UNSIGNED)";

    /**
     * SQL statement to add a tool.
     */
    private static final String ADD_TOOL_SQL = "INSERT INTO Tools VALUES" +
            "(NULL, ?, ?, ?, ?)";

    private static final String UPDATE_TOOL_SQL = "UPDATE Tools" +
            " SET NAME=?, NUM_INPUT_TABLES=?, NUM_PARAMETERS=?, PARAMETER_TYPES=?" +
            " WHERE ID=?";

    /**
     * SQL statement to get all tools.
     */
    private static final String GET_TOOLS_SQL = "SELECT * FROM TOOLS";

    /**
     * SQL statement to get a tool by id.
     */
    private static final String GET_TOOL_BY_ID_SQL = "SELECT * FROM TOOLS WHERE id=?";

    /**
     * SQL statment to get all tables.
     */
    private static final String GET_TABLES_SQL = "SELECT name FROM sqlite_master WHERE type='table'";

    /**
     * SQL statement to insert a row into a data table.
     */
    private static final String INSERT_ROW_SQL = "INSERT INTO ? VALUES (?)";

    /**
     * SQL statement to get all rows from a table.
     */
    private static final String GET_ALL_ROWS_SQL = "SELECT * FROM ?";

    private String sqlClass;
    private String systemConnection;
    private String dataConnection;

    /**
     * Creates a new DbManager and initializes its connection to test.db.
     */
    public DbManager(String sqlClass, String systemConnection, String dataConnection) {
        this.sqlClass = sqlClass;
        this.systemConnection = systemConnection;
        this.dataConnection = dataConnection;
    }

    /**
     * Ensures that the necessary tables have been created in test.db. Should be called after construction.
     */
    public void initialize() {
        Connection conn;
        try {
            // Open a connection to the db
            Class.forName(sqlClass);
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
            Class.forName(sqlClass);
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
            Class.forName(sqlClass);
            conn = DriverManager.getConnection(systemConnection);

            // Create the table
            PreparedStatement s = conn.prepareStatement(ADD_TOOL_SQL);
            s.setString(1, tool.getName());
            s.setInt(2, tool.getNumTables());
            s.setInt(3, tool.getNumParams());
            s.setString(4, tool.getParamTypes()
                    .stream()
                    .map(Parameter.ParameterType::toString)
                    .collect(Collectors.joining()));
            s.executeUpdate();

            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void updateTool(Tool tool) {
        Connection conn;
        try {
            // Open a connection to the db
            Class.forName(sqlClass);
            conn = DriverManager.getConnection(systemConnection);

            // Create the table
            PreparedStatement s = conn.prepareStatement(UPDATE_TOOL_SQL);
            s.setString(1, tool.getName());
            s.setInt(2, tool.getNumTables());
            s.setInt(3, tool.getNumParams());
            s.setString(4, tool.getParamTypes()
                    .stream()
                    .map(Parameter.ParameterType::toString)
                    .collect(Collectors.joining()));
            s.setInt(5, tool.getToolId());
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
            Class.forName(sqlClass);
            conn = DriverManager.getConnection(systemConnection);

            // Create the table
            Statement s = conn.createStatement();
            ResultSet results = s.executeQuery(GET_TOOLS_SQL);

            HashSet<Tool> tools = new HashSet<>();
            while (results.next()) {
                String name = results.getString(2);
                int numTables = results.getInt(3);

                //Tool tool = new Tool(name, numTables);
                tools.add(null);
            }

            conn.close();
            return tools;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return null;
    }

    public Tool getToolById(int toolId) {
        try (Connection conn = DriverManager.getConnection(systemConnection)){
            // Create the table
            PreparedStatement s = conn.prepareStatement(GET_TOOL_BY_ID_SQL);
            s.setInt(1, toolId);
            ResultSet results = s.executeQuery();

            while (results.next()) {
                String toolName = results.getString(2);
                int numInputTables = results.getInt(3);
                int numParameters = results.getInt(4);
                String parameterTypesString = results.getString(5);
                List<Parameter.ParameterType> parameterTypes = Arrays.stream(parameterTypesString.split(","))
                        .map(Parameter.ParameterType::valueOf)
                        .collect(Collectors.toList());

                Tool tool = new Tool(toolId, toolName, numInputTables, numParameters, parameterTypes);

                return tool;
            }

            conn.close();
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
            Class.forName(sqlClass);
            conn = DriverManager.getConnection(dataConnection);

            // Create the table
            Statement s = conn.createStatement();
            ResultSet results = s.executeQuery(GET_TABLES_SQL);

            HashSet<Table> tables = new HashSet<>();
            while (results.next()) {
                //TODO: zero-indexed?
                String name = results.getString(1);

                //Table table = new Table(name);
                tables.add(null);
            }

            conn.close();
            return tables;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return null;
    }

    public ArrayList<Tuple> getAllRows(String tableName) {
        Schema schema = this.getSchema(tableName);

        Connection conn;
        try {
            // Open a connection to the db
            Class.forName(sqlClass);
            conn = DriverManager.getConnection(dataConnection);

            // Get the rows
            Statement s = conn.createStatement();
            ResultSet results = s.executeQuery(GET_ALL_ROWS_SQL);

            conn.close();

            ArrayList<Tuple> rows = new ArrayList<>();
            while(results.next()) {
                Tuple t = new Tuple(schema, results);
                rows.add(t);
            }
            return rows;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return null;
    }

    public void addRow(Table table, Tuple t) {
        Connection conn;
        try {
            // Open a connection to the db
            Class.forName(sqlClass);
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

    public Schema getSchema(String tableName) {
        Connection conn;
        try {
            // Open a connection to the db
            Class.forName(sqlClass);
            conn = DriverManager.getConnection(dataConnection);

            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet result = dbmd.getColumns(null, null, tableName, null);

            ArrayList<Column> cols = new ArrayList<>();
            while(result.next()) {
                String colName = result.getString(4);
                int colType = result.getInt(5);
                Column col = new Column(tableName, colName, colType);
                cols.add(col);
            }

            Schema schema = new Schema(cols);

            conn.close();
            return schema;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return null;
    }
}
