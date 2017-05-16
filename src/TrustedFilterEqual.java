import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by mdaigle on 5/10/17.
 */
public class TrustedFilterEqual <T> extends Tool{
    private Table inputTable;
    private String outputTableName;
    private ResultSet resultSet;
    private Predicate predicate;

    public TrustedFilterEqual(String toolName, int numTables, Table inputTable, String outputTableName, Predicate predicate) {
        super(toolName, numTables);
        this.inputTable = inputTable;
        this.outputTableName = outputTableName;
        this.predicate = predicate;
    }

    public ArrayList<Row> run() {
        ArrayList<Tuple> inputRows = inputTable.getRows();
        ArrayList<Row> outputRows = new ArrayList<>();

        for(Tuple t : inputRows) {
            if (predicate.filter(t)) {
                RowProvenance rp = new RowProvenance(new Table[]{inputTable});
                Row r = new Row(t, rp);
                outputRows.add(r);
            }
        }

        return outputRows;
    }
}
