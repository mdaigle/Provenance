import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableMetadataDeserializer implements JsonDeserializer<TableMetadata> {
    @Override
    public TableMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        int numCols = jsonObject.get("numCols").getAsInt();
        int toolId = jsonObject.get("toolId").getAsInt();

        JsonArray jsonInputTableMetadata = jsonObject.get("inputTables").getAsJsonArray();
        InputTableMetadata[] inputTableMetadata = new InputTableMetadata[jsonInputTableMetadata.size()];
        for (int i = 0; i < inputTableMetadata.length; i++) {
            JsonObject jsonTable = jsonInputTableMetadata.get(i).getAsJsonObject();
            inputTableMetadata[i] = jsonDeserializationContext.deserialize(jsonTable, InputTableMetadata.class);
        }
        //TODO: just make a second constructor
        ArrayList<InputTableMetadata> inputTableMetadataArray = new ArrayList<>();
        Collections.addAll(inputTableMetadataArray, inputTableMetadata);

        JsonArray jsonParameters = jsonObject.get("parameters").getAsJsonArray();
        List<Parameter> parameters = new ArrayList<>();
        jsonParameters.forEach(jsonParameter -> {
            parameters.add(jsonDeserializationContext.deserialize(jsonParameter, Parameter.class));
        });

        return new TableMetadata(numCols, toolId, inputTableMetadataArray, parameters);
    }
}
