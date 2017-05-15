import java.util.ArrayList;

/**
 *  Represents a real table in the data-database. Each Table should have a unique name (used as identifier).
 *  TODO: hash id instead or something, because user generated ids suck
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
        return name;
    }

    public ToolInstance getTool() {
        return tool;
    }

    public ArrayList<Tuple> getRows() {
        return ProvenanceSystem.getDbManager().getAllRows(name);
    }

    public void addRows(ArrayList<Tuple> rows) {
        for (Tuple t : rows) {
            this.addRow(t);
        }
    }

    public void addRow(Tuple t) {
        ProvenanceSystem.getDbManager().addRow(this, t);
    }

    public Schema getSchema() {
        return ProvenanceSystem.getDbManager().getSchema(name);
    }
}
