import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mdaigle on 4/10/17.
 */
public class ToolInstance {
    private Tool tool;
    private HashSet<Table> tables;

    /**
     * Creates a new instance of the given tool using the given tables.
     * @param tool
     * @param tables
     */
    public ToolInstance(Tool tool, Table[] tables) {
        if (tool.getNumTables() != tables.length) {
            throw new IllegalArgumentException("Incorrect number of tables for tool.");
        }

        this.tool = tool;
        this.tables = new HashSet<>();

        Collections.addAll(this.tables, tables);
    }

    /**
     * Returns the tool of which this is an instance.
     * @return
     */
    public Tool getTool() {
        return this.tool;
    }

    /**
     * Returns the tables that this tool instance uses.
     * @return
     */
    public Set<Table> getTables() {
        return this.tables;
    }
}
