/**
 * Created by mdaigle on 4/5/17.
 */
public class Table {
    private String name;
    private ToolInstance tool;

    /**
     * Creates a new base Table.
     * @param name
     */
    public Table(String name) {
        this.name = name;
        this.tool = null;
    }

    /**
     * Creates a new derived Table and records its dependencies
     * @param name
     * @param tool
     */
    public Table(String name, ToolInstance tool) {
        this.name = name;
        this.tool = tool;
    }

    /**
     * Returns the table's name.
     * @return the table's name
     */
    public String getName() {
        return this.name;
    }

    public ToolInstance getTool() {
        return this.tool;
    }
}
