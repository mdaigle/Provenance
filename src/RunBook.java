import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class RunBook {
    private List<Step> steps;

    public RunBook() {
        this.steps = new ArrayList<>();
    }

    public void addStep(Step step) {
        steps.add(step);
    }

    public void play() {
        for (Step step : steps) {
            step.run();
        }
    }

    public static RunBook fromJson(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(RunBook.class, new RunBookDeserializer());
        Gson gson = gsonBuilder.create();
        return gson.fromJson(json, RunBook.class);
    }
}
