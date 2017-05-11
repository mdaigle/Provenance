import java.sql.ResultSet;

/**
 * Created by mdaigle on 5/10/17.
 */
public class TrustedFilterEqual <T> extends Tool{
    private Table inputTable;
    private String outputTableName;
    private ResultSet

    public TrustedFilterEqual(String toolName, int numTables, Table inputTable, String outputTableName) {
        super(toolName, numTables);
        this.inputTable = inputTable;
        this.outputTableName = outputTableName;
    }

    public getNext() {

    }
}
