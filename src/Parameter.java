import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Parameter {
    private ParameterType type;
    private String value;

    enum ParameterType {
        STRING, INTEGER, PREDICATE
    }

    Parameter(ParameterType type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public ParameterType getType() {
        return type;
    }


    public String toJSON() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

    /**
     * Deserializes the provided Parameter serialization.
     * @param json
     */
    public static Parameter fromJson(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Parameter.class, new ParameterDeserializer());
        Gson gson = gsonBuilder.create();
        return gson.fromJson(json, Parameter.class);
    }
}
