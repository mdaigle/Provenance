import java.util.*;

/**
 * Created by mdaigle on 4/5/17.
 */
public class ProvenanceManager {
    /**
     * All tables managed by this provenance manager.
     */
    private Set<Table> tables;

    /**
     * Maps base tables to tables derived from them (i.e. tables that depend on them).
     */
    private Map<Table, HashSet<Table>> dependencies;

    /**
     * All tools available for use with tables tracked by this provenance manager.
     */
    private Set<Tool> tools;

    /**
     * Create a new provenance manager.
     */
    public ProvenanceManager() {
        this.tables = new HashSet<>();
        this.dependencies = new HashMap<>();
        this.tools = new HashSet<>();
    }

    /**
     * Returns all tables that depend on the given base table
     * @param base
     * @return
     */
    public Set<Table> getDependencies(Table base) {
        return Collections.unmodifiableSet(this.dependencies.get(base));
    }

    /**
     * Adds a dependency. The derived table depends on the base table.
     * @param base the base table
     * @param derived the derived table
     */
    public void addDependency(Table base, Table derived) {
        HashSet<Table> set = this.dependencies.get(base);

        if (set == null) {
            set = new HashSet<>();
        }

        set.add(derived);
        dependencies.put(base, set);
    }

    /**
     * Returns the table managed by this provenance manager.
     * @return
     */
    public Set<Table> getTables() {
        return Collections.unmodifiableSet(this.tables);
    }

    /**
     * Adds a table.
     * @param table
     */
    public boolean addTable(Table table) {
        if (tables.contains(table)) {
            return false;
        }

        return this.tables.add(table);
    }

    /**
     * Adds a derived table and its dependencies.
     * @param derived
     * @param ti
     */
    public boolean addTable(Table derived, ToolInstance ti) {
        if (!tools.contains(ti.getTool())) {
            throw new IllegalArgumentException("ToolInstance's Tool type is not recognized.");
        }

        if (!addTable(derived)) {
            return false;
        }

        for (Table base : ti.getTables()) {
            this.addDependency(base, derived);
        }

        return true;
    }

    /**
     * Returns the set of available tools.
     * @return
     */
    public Set<Tool> getTools() {
        return Collections.unmodifiableSet(this.tools);
    }

    /**
     * Adds a new tool to the set of available tools.
     * @param tool
     */
    public void addTool(Tool tool) {
        this.tools.add(tool);
    }
}
