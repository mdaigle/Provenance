import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

/**
 * An in-memory table-table level provenance modeler and manager
 */
public class ProvenanceManager {
    /**
     * All tables managed by this provenance manager.
     */
    //private Set<Table> tables;

    /**
     * Maps base tables to tables derived from them (i.e. tables that depend on them).
     */
    private Map<TableHeader, Set<TableHeader>> dependencies;

    /**
     * All tools available for use with tables tracked by this provenance manager.
     */
    private Set<Tool> tools;

    public static class ImpactedTables {
        private Set<TableHeader> definitelyImpacted;
        private Set<TableHeader> possiblyImpacted;

        public ImpactedTables() {
            definitelyImpacted = new HashSet<>();
            possiblyImpacted = new HashSet<>();
        }

        public void addDefinitelyImpacted(TableHeader header) {
            definitelyImpacted.add(header);
        }

        public void addDefinitelyImpacted(Collection<TableHeader> headers) {
            definitelyImpacted.addAll(headers);
        }

        public void addPossiblyImpacted(TableHeader header) {
            possiblyImpacted.add(header);
        }

        public void addPossiblyImpacted(Collection<TableHeader> headers) {
            possiblyImpacted.addAll(headers);
        }

        public Set<TableHeader> getDefinitelyImpacted() {
            return Collections.unmodifiableSet(definitelyImpacted);
        }

        public Set<TableHeader> getPossiblyImpacted() {
            return Collections.unmodifiableSet(possiblyImpacted);
        }
    }

    /**
     * Create a new provenance manager.
     */
    public ProvenanceManager() {
        // TODO: add parameter for connection string?
        //this.tables = new HashSet<>();
        this.dependencies = new HashMap<>();
        this.tools = new HashSet<>();
    }

    /**
     * Initializes the backing database and loads stored state.
     */
    public void initialize() {
        dependencies = ProvenanceSystem.getDbManager().getDependencies();
    }

    /**
     * Save the current dependency state to the db.
     */
    public void save() {
        ProvenanceSystem.getDbManager().saveDependencies(dependencies);
    }

    /**
     * Returns all tables that depend on the given base table
     * @param base
     * @return
     */
    public Set<TableHeader> getDependencies(TableHeader base) {
        Set<TableHeader> dependencies = this.dependencies.get(base);

        return dependencies == null ? null : Collections.unmodifiableSet(dependencies);
    }

    public Set<TableHeader> getDependenciesRecursive(TableHeader base) {
        Set<TableHeader> dependentTables = new HashSet<>();

        List<TableHeader> toCheck = new ArrayList<>();
        toCheck.addAll(getDependencies(base));

        while (!toCheck.isEmpty()) {
            TableHeader next = toCheck.remove(0);
            Set<TableHeader> nextDependents = getDependencies(next);
            dependentTables.addAll(nextDependents);
            toCheck.addAll(nextDependents);
        }

        return dependentTables;
    }

    /**
     * Adds a dependency. The derived table depends on the base table.
     * @param base the base table
     * @param derived the derived table
     */
    public void addDependency(TableHeader base, TableHeader derived) {
        Set<TableHeader> set = this.dependencies.get(base);

        if (set == null) {
            set = new HashSet<>();
        }

        set.add(derived);
        dependencies.put(base, set);
    }

    /**
     * Adds dependencies.
     * @param base
     * @param inputs
     */
    public void addDependencies(TableHeader base, List<TableHeader> inputs) {
        inputs.forEach(input -> addDependency(base, input));
    }

    public Set<TableHeader> getDependentTables(TableHeader base) {
        return dependencies.get(base);
    }

    private void removeDependency(TableHeader base, TableHeader derived) {
        Set<TableHeader> dependencySet = dependencies.get(base);

        if (dependencySet == null) {
            return;
        }

        dependencySet.remove(derived);
    }

    /*public void revokeAccess(Table table) {
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

            // TODO: something more sophisticated than just deleting dependencies.
            // Maybe have flags set on tables that indicate if they have access to all of the tables they depend on.
            dependencies.remove(curr);
        }
    }*/

    public ImpactedTables getImpactedTables(EditHistory editHistory) {
        ImpactedTables impactedTables = new ImpactedTables();
        Set<TableHeader> directDependencies = getDependencies(editHistory.getTableHeader());

        if (directDependencies == null) {
            return impactedTables;
        }

        for (TableHeader header : directDependencies) {
            Table dependentTable = Table.getTable(header);
            if (!dependentTable.impactedBy(editHistory)) {
                continue;
            }

            impactedTables.addDefinitelyImpacted(header);
            impactedTables.addPossiblyImpacted(getDependenciesRecursive(header));
        }

        return impactedTables;
    }
}
