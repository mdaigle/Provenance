import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

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
        Parameter[] parameters = new Parameter[jsonParameters.size()];
        for (int i = 0; i < parameters.length; i++) {
            JsonObject jsonParam = jsonParameters.get(i).getAsJsonObject();
            Parameter.ParameterType parameterType = Parameter.ParameterType.valueOf(jsonParam.get("type").getAsString());
            Type paramClass = Parameter.getClassForType(parameterType);
            parameters[i] = jsonDeserializationContext.deserialize(jsonParam, paramClass);
        }
        return new TableMetadata(numCols, toolId, inputTableMetadataArray, parameters);
    }
}
