/**
 * Created by mdaigle on 4/5/17.
 */
public class Column {

    private Table table;
    private String name;

    public Column(Table table, String name) {
        this.table = table;
        this.name = name;
    }

    public Table getTable() {
        return this.table;
    }

    public String getName() {
        return this.name;
    }
}
