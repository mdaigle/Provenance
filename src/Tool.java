import java.util.ArrayList;

/**
 * Created by mdaigle on 4/5/17.
 */
public class Tool {
    private ArrayList<Column> cols;

    public Tool(ArrayList<Column> cols) {
        this.cols = cols;
    }

    public Tool(Table[] tables) {
        this.cols = new ArrayList<>();

        for (Table table : tables) {
            for (Column col : table.getCols()) {
                this.cols.add(col);
            }
        }
    }

    public ArrayList<Column> getCols() {
        return this.cols;
    }
}
