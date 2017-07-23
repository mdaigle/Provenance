import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jdk.internal.util.xml.impl.Input;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by mdaigle on 5/17/17.
 */
public class TableMetadata {
    int numCols;
    int toolId;
    List<InputTableMetadata> inputTables;
    List<Parameter> parameters;

    /*
    For projection this looks like:
    toolId = 1 (projection)
    inputTableIds = {"a1b2c3..."} (just one id belonging to the origin table)
    parameters = {ColParam("col1"), ColParam("col2"), ...} (the columns to project)
     */

    /*
    For general tool this looks like:
    toolId = 123 (id of the tool used to create the table)
    inputTableIds = {"a1b2", "c3d4", ...} (the ids of the tables fed to the tool)
    parameters = {Param(p1), Param(p2), ...} (parameters for the tool. can be concrete values, predicates, etc.)
     */

    /**
     *
     * @param inputTables
     * @param parameters
     */
    public TableMetadata(int numCols, int toolId, List<InputTableMetadata> inputTables, List<Parameter> parameters) {
        this.numCols = numCols;
        this.toolId = toolId;
        this.inputTables = inputTables;
        this.parameters = parameters;
    }

    /**
     * Determines if the table is impacted by the edit history.
     * Returns true if definitely impacted, null if possibly
     * impacted, and false if definitely not impacted.
     * @param editHistory
     * @return
     */
    public Boolean impactedBy(EditHistory editHistory) {
        for (InputTableMetadata metadata : inputTables) {
            if (!metadata.tableHeader.equals(editHistory.getTableHeader())) {
                continue;
            }

            if (metadata.dependsOnAllRows()) {
                return true;
            }

            if (metadata.dependsOnGreaterThanFive()) {
                return null;
            }

            for (EditHistory.Edit edit : editHistory) {
                if (metadata.rows.contains(edit.getLineNumber())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Deserializes the provided TableMetadata serialization.
     * @param json
     */
    public static TableMetadata fromJson(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(TableMetadata.class, new TableMetadataDeserializer());
        Gson gson = gsonBuilder.create();
        return gson.fromJson(json, TableMetadata.class);
    }

    /**
     * Converts condensed table metadata into a table metadata object.
     * Format should be:
     *
     * numCols
     * tableid numRows (ALL, >5) [or tableid rownum rownum ... (individuals, <=5)]
     *
     * @param condensed
     * @return
     */
    public static TableMetadata fromCondensed(String condensed, int numCols, int toolId, List<Parameter> params) {
        String[] lines = condensed.split("\n");

        ArrayList<InputTableMetadata> inputTablesArray = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            // component 0 is tableid, component 1 is rows (ALL, >5, or individuals)
            String[] components = lines[i].split("\t", 2);
            List<String> rows = Arrays.asList(components[1].split("\\s+"));

            TableHeader header = new TableHeader();
            header.setName(components[0]);

            InputTableMetadata input = new InputTableMetadata(header, rows);
            inputTablesArray.add(input);
        }

        return new TableMetadata(numCols, toolId, inputTablesArray, params);
    }

    public String toJSON() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }
}
