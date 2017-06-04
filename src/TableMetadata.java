import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jdk.internal.util.xml.impl.Input;

import java.util.ArrayList;

/**
 * Created by mdaigle on 5/17/17.
 */
public class TableMetadata {
    int numCols;
    int toolId;
    InputTableMetadata[] inputTables;
    Parameter[] parameters;

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
    public TableMetadata(int numCols, int toolId, InputTableMetadata[] inputTables, Parameter[] parameters) {
        this.numCols = numCols;
        this.toolId = toolId;
        this.inputTables = inputTables;
        this.parameters = parameters;
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
    public static TableMetadata fromCondensed(String condensed, int toolId, Parameter[] params) {
        String[] lines = condensed.split("\n");

        int numCols = lines.length > 0 ? Integer.parseInt(lines[0]) : 0;

        ArrayList<InputTableMetadata> inputTables = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String[] components = lines[i].split(" ", 1);
            InputTableMetadata input = new InputTableMetadata(components[0], components[1]);
            inputTables.add(input);
        }
        InputTableMetadata[] inputTablesArray = (InputTableMetadata[]) inputTables.toArray();

        return new TableMetadata(numCols, toolId, inputTablesArray, params);
    }

    public String toJSON() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }
}
