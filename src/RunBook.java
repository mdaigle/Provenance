import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    public static RunBook readInRunBook(String runBookName) {
        RunBook rb;
        try {
            String content = new String(Files.readAllBytes(Paths.get(Main.RUNBOOK_DIR + runBookName + ".runbook")));
            rb = RunBook.fromJson(content);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return new RunBook();
        }

        return rb;
    }
}
