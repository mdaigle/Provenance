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
        Set<Table> dependencies = this.dependencies.get(base);

        if (dependencies == null) {
            return null;
        }

        return Collections.unmodifiableSet(dependencies);
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

    public void revokeAccess(Table table) {
        if (!this.tables.contains(table)) {
            return;
        }

        ArrayList<Table> toRevoke = new ArrayList<>();
        toRevoke.add(table);

        while (!toRevoke.isEmpty()) {
            Table curr = toRevoke.remove(0);
            Set<Table> dependentTables = this.dependencies.get(curr);

            if (dependentTables == null) {
                continue;
            }

            toRevoke.addAll(dependentTables);

            /* TODO: something more sophisticated than just deleting dependencies.
             * Maybe have flags set on tables that indicate if they have access to all of the tables they depend on.
             */
            dependencies.remove(curr);

        }
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
