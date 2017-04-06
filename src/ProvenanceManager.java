import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by mdaigle on 4/5/17.
 */
public class ProvenanceManager {
    private HashMap<Column, HashSet<Table>> dependencies;

    public ProvenanceManager() {
        this.dependencies = new HashMap<>();
    }

    public HashSet<Table> getDependencies(Column col) {
        return this.dependencies.get(col);
    }

    public void addDependency(Column col, Table table) {
        HashSet<Table> set = getDependencies(col);

        if (set == null) {
            set = new HashSet<>();
        }

        set.add(table);

        dependencies.put(col, set);
    }
}
