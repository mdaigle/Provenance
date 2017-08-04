import java.util.ArrayList;
import java.util.List;

public class Step {
    private String toolName;
    private List<Parameter> parameters;
    private List<String> inputFileNames;
    private List<List<String>> inputFileColumns;
    private String outputFileName;

    public Step(String toolName, List<Parameter> parameters, List<String> inputFileNames, List<List<String>> inputFileColumns, String outputFileName) {
        this.toolName = toolName;
        this.parameters = parameters;
        this.inputFileNames = inputFileNames;
        this.inputFileColumns = inputFileColumns;
        this.outputFileName = outputFileName;
    }

    public void run() {
        List<Table> inputTables = new ArrayList<>();

        for (int i = 0; i < inputFileNames.size(); i++) {
            inputTables.add(Table.getTable(inputFileNames.get(i), inputFileColumns.get(i)));
        }

        Tool tool = ProvenanceSystem.getDbManager().getToolByName(toolName);

        Tool.ToolOutput output = tool.run(inputTables, parameters, outputFileName);
    }


}
