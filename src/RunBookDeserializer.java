import com.google.gson.*;

import java.lang.reflect.Type;

public class RunBookDeserializer implements JsonDeserializer<RunBook> {
    @Override
    public RunBook deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Gson gson = new Gson();
        RunBook runBook = new RunBook();

        final JsonArray jsonArray = jsonElement.getAsJsonArray();
        for (JsonElement stepElement : jsonArray) {
            Step step = gson.fromJson(stepElement, Step.class);
            runBook.addStep(step);
        }

        return runBook;
    }
}
