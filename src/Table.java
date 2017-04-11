/**
 * Created by mdaigle on 4/5/17.
 */
public class Table {
    private String name;
    private ToolInstance tool;

    /**
     * Creates a new base Table.
     * @param name
     * @param pm
     */
    public Table(String name, ProvenanceManager pm) {
        this.name = name;
        pm.addTable(this);
    }

    /**
     * Creates a new derived Table and records its dependencies
     * @param name
     * @param tool
     * @param pm
     */
    public Table(String name, ToolInstance tool, ProvenanceManager pm) {
        this.name = name;
        this.tool = tool;
        pm.addTable(this, tool);
    }

    /**
     * Returns the table's name.
     * @return the table's name
     */
    public String getName() {
        return this.name;
    }
}
