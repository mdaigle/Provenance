/**
 * Created by mdaigle on 4/5/17.
 */
public class Tool {
    /**
     * The name of this tool.
     */
    private String name;

    /**
     * The number of tables this tool takes as input.
     */
    private int numTables;

    /**
     * Creates a new Tool with given name and expected number of input tables.
     * @param name
     * @param numTables
     */
    public Tool(String name, int numTables) {
        this.name = name;
        this.numTables = numTables;
    }

    /**
     * Returns the name of this tool.
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the number of tables this tool takes as input.
     * @return
     */
    public int getNumTables() {
        return this.numTables;
    }
}
