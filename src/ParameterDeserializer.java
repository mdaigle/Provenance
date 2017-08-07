import com.google.gson.*;

import java.lang.reflect.Type;

public class ParameterDeserializer implements JsonDeserializer<Parameter> {
    @Override
    public Parameter deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        Parameter.ParameterType parameterType = Parameter.ParameterType.values()[jsonObject.get("type").getAsInt()];
        String value = jsonObject.has("value") ? jsonObject.get("value").getAsString() : "";
        String name = jsonObject.has("name") ? jsonObject.get("name").getAsString() : "";

        return new Parameter(parameterType, value, name);
    }
}