/**
 * Created by mdaigle on 4/5/17.
 */
public class Table {
    private Column[] cols;
    private String name;
    private Tool tool;

    public Table(String name, int num_cols) {
        this.cols = new Column[num_cols];

        for (int i = 0; i < num_cols; i++) {
            this.cols[i] = new Column(this, i + "");
        }

        this.name = name;
    }

    public Table(String name, int num_cols, Tool tool, ProvenanceManager pm) {
        this(name, num_cols);
        this.tool = tool;

        for (Column col : this.tool.getCols()) {
            pm.addDependency(col, this);
        }
    }

    public int getNumCols() {
        return this.cols.length;
    }

    public Column[] getCols() {
        return cols;
    }

    public Column getCol(int index) {
        return cols[index];
    }
}
