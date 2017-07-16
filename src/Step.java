public class Step {
    private String toolName;
    private Parameter[] parameters;
    private String[] inputFileNames;
    private String[][] inputFileColumns;
    private String outputFileName;

    public Step(String toolName, Parameter[] parameters, String[] inputFileNames, String[][] inputFileColumns, String outputFileName) {
        this.toolName = toolName;
        this.parameters = parameters;
        this.inputFileNames = inputFileNames;
        this.inputFileColumns = inputFileColumns;
        this.outputFileName = outputFileName;
    }

    public void run() {
        Table[] inputTables = new Table[inputFileNames.length];

        for (int i = 0; i < inputTables.length; i++) {
            inputTables[i] = Table.getTable(inputFileNames[i], inputFileColumns[i]);
        }

        Tool tool = ProvenanceSystem.getDbManager().getToolByName(toolName);

        Tool.ToolOutput output = tool.run(inputTables, parameters);

        Tool.writeOutput(outputFileName, output);
    }


}
