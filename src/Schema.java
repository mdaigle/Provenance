import java.util.ArrayList;

/**
 * Created by mdaigle on 5/10/17.
 */
public class Schema {
    private ArrayList<Column> cols;

    public Schema(ArrayList<Column> cols) {
        this.cols = cols;
    }

    public int size() {
        return cols.size();
    }

    public String getName(int index) {
        return cols.get(index).name;
    }

    public int getType(int index) {
        return cols.get(index).type;
    }
}
