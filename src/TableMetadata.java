import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by mdaigle on 5/17/17.
 */
public class TableMetadata {
    int toolId;
    String[] inputTableIds;
    Parameter[] parameters;
    /*
    For projection this looks like:
    toolId = 0 (projection)
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
     * @param inputTableIds
     * @param parameters
     */
    public TableMetadata(int toolId, String[] inputTableIds, Parameter[] parameters) {
        this.toolId = toolId;
        this.inputTableIds = inputTableIds;
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

    public String toJSON() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }
}
