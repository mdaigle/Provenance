import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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
    private static final String CREATE_TOOLS_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS tools" +
                    "(id                INTEGER     PRIMARY KEY," +
                    "name               TEXT        KEY," +
                    "num_input_tables   INTEGER     UNSIGNED," +
                    "num_parameters     INTEGER     UNSIGNED," +
                    "parameter_types    TEXT        NOT NULL)";

    private static final String CREATE_DEPENDENCIES_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS dependencies" +
                    "(base      TEXT        PRIMARY KEY," +
                    "derived    TEXT        NOT NULL)";

    /**
     * SQL statement to create a data table.
     */
    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS ?" +
            "(id                INTEGER     PRIMARY KEY, " +
            "value              INTEGER     UNSIGNED)";

    /**
     * SQL statement to add a tool.
     */
    private static final String ADD_TOOL_SQL =
            "INSERT INTO tools VALUES" +
            "(NULL, ?, ?, ?, ?)";

    private static final String UPDATE_TOOL_SQL =
            "UPDATE tools" +
            " SET NAME=?, NUM_INPUT_TABLES=?, NUM_PARAMETERS=?, PARAMETER_TYPES=?" +
            " WHERE ID=?";

    private static final String UPDATE_OR_INSERT_DEPENDENCIES_BY_BASE =
            "INSERT OR REPLACE INTO dependencies VALUES (?, ?)";

    /**
     * SQL statement to get all tools.
     */
    private static final String GET_TOOLS_SQL = "SELECT * FROM TOOLS";

    /**
     * SQL statement to get a tool by id.
     */
    private static final String GET_TOOL_BY_ID_SQL = "SELECT * FROM TOOLS WHERE id=?";

    /**
     * SQL statement to get all dependencies.
     */
    private static final String GET_ALL_DEPENDENCIES = "SELECT * FROM DEPENDENCIES";

    /**
     * SQL statement to get all dependencies for a base table (by name).
     */
    private static final String GET_DEPENDENCIES_BY_BASE = "SELECT * FROM dependencies WHERE base=?";

    /**
     * SQL statement to get a tool by name.
     */
    private static final String GET_TOOL_BY_NAME_SQL = "SELECT * FROM TOOLS WHERE name=?";

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

            // Create Dependencies table
            s = conn.createStatement();
            s.executeUpdate(CREATE_DEPENDENCIES_TABLE_SQL);

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
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void updateOrAddDependencies(TableHeader base, Set<TableHeader> derived) {
        Connection conn;
        try {
            // Open a connection to the db
            Class.forName(sqlClass);
            conn = DriverManager.getConnection(systemConnection);

            Gson gson = new Gson();

            // Create the table
            PreparedStatement s = conn.prepareStatement(UPDATE_OR_INSERT_DEPENDENCIES_BY_BASE);
            s.setString(1, gson.toJson(base));
            s.setString(2, gson.toJson(derived));
            s.executeUpdate();

            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void saveDependencies(Map<TableHeader, Set<TableHeader>> dependencies) {
        for (TableHeader base : dependencies.keySet()) {
            updateOrAddDependencies(base, dependencies.get(base));
        }
    }

    public Map<TableHeader, Set<TableHeader>> getDependencies() {
        try (Connection conn = DriverManager.getConnection(systemConnection)){
            Map<TableHeader, Set<TableHeader>> map = new HashMap<>();

            PreparedStatement s = conn.prepareStatement(GET_ALL_DEPENDENCIES);
            ResultSet results = s.executeQuery();

            while (results.next()) {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                String baseJSON = results.getString(1);
                TableHeader base = gson.fromJson(baseJSON, TableHeader.class);

                String dependenciesJSONString = results.getString(2);


                Set<TableHeader> dependentTables = gson.fromJson(dependenciesJSONString, new TypeToken<Set<TableHeader>>(){}.getType());

                map.put(base, dependentTables);
            }

            return map;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return null;
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

            List<String> params = tool.getParameters()
                    .stream()
                    .map(Parameter::toJSON)
                    .collect(Collectors.toList());
            String paramString = new Gson().toJson(params);

            s.setString(4, paramString);
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

            List<String> params = tool.getParameters()
                    .stream()
                    .map(Parameter::toJSON)
                    .collect(Collectors.toList());
            String paramString = new Gson().toJson(params);

            s.setString(4, paramString);
            s.setInt(5, tool.getToolId());
            s.executeUpdate();

            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public Collection<String> getTools() {
        Connection conn;
        try {
            // Open a connection to the db
            Class.forName(sqlClass);
            conn = DriverManager.getConnection(systemConnection);

            // Create the table
            Statement s = conn.createStatement();
            ResultSet results = s.executeQuery(GET_TOOLS_SQL);

            List<String> tools = new ArrayList<>();
            while (results.next()) {
                String id = results.getString(1);
                String name = results.getString(2);
                String numTables = results.getString(3);
                String params = results.getString(5);
                tools.add(String.format("%s\t%s\t\t%s\t%s", id, name, numTables, params));
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
                String parametersString = results.getString(5);
                List<String> parametersJsonArray = new Gson().fromJson(parametersString, new TypeToken<List<String>>() {}.getType());
                List<Parameter> parameters = parametersJsonArray.stream()
                        .map(p -> new Gson().fromJson(p, Parameter.class))
                        .collect(Collectors.toList());

                Tool tool = new Tool(toolId, toolName, numInputTables, parameters);

                return tool;
            }

            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return null;
    }

    public Tool getToolByName(String toolName) {
        try (Connection conn = DriverManager.getConnection(systemConnection)){
            // Create the table
            PreparedStatement s = conn.prepareStatement(GET_TOOL_BY_NAME_SQL);
            s.setString(1, toolName);
            ResultSet results = s.executeQuery();

            while (results.next()) {
                int toolId = results.getInt(1);
                int numInputTables = results.getInt(3);
                int numParameters = results.getInt(4);
                String parametersString = results.getString(5);
                List<String> parametersJsonArray = new Gson().fromJson(parametersString, new TypeToken<List<String>>() {}.getType());
                List<Parameter> parameters = parametersJsonArray.stream()
                        .map(p -> new Gson().fromJson(p, Parameter.class))
                        .collect(Collectors.toList());

                Tool tool = new Tool(toolId, toolName, numInputTables, parameters);

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
