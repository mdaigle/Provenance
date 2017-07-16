import com.google.gson.*;

import java.lang.reflect.Type;

public class StepDeserializer implements JsonDeserializer<Step> {

    @Override
    public Step deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        /*Gson gson = new Gson();
        final JsonObject jsonObject = jsonElement.getAsJsonObject();

        String toolName = jsonObject.get("toolName").getAsString();

        JsonElement paramsElement = jsonObject.get("parameters");
        Parameter[] parameters = gson.fromJson(paramsElement, Parameter[].class);

        JsonElement fileNamesElement = jsonObject.get("inputFileNames");
        String[] fileNames = gson.fromJson(fileNamesElement, String[].class);

        JsonElement fileColumnsElement = jsonObject.get("inputFileColumns");
        String[][] fileColumns = gson.fromJson(fileColumnsElement, String[][].class);

        String outputFileName = jsonObject.get("outputFileName").getAsString();

        return new Step(toolName, parameters, fileNames, fileColumns, outputFileName);*/
        return null;
    }
}