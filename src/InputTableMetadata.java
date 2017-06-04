/**
 * Created by mdaigle on 6/3/17.
 */
public class InputTableMetadata {
    String tableId;
    String numContributingRows;

    InputTableMetadata(String tableId, String contributingRows) {
        this.tableId = tableId;
        this.numContributingRows = contributingRows;
    }
}
