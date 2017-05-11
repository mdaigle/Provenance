/**
 * Created by mdaigle on 4/5/17.
 */
public class Column {

    public final String tableName;
    public final String name;
    public final int type;

    public Column(String tableName, String name, int type) {
        this.tableName = tableName;
        this.name = name;
        this.type = type;
    }
}
