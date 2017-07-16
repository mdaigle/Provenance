public class Step {
    private Tool tool;
    private Parameter[] parameters;
    private String[] inputFileNames;
    private String[][] inputFileColumns;

    public Step(Tool tool, Parameter[] parameters, String[] inputFileNames, String[][] inputFileColumns) {
        this.tool = tool;
        this.parameters = parameters;
        this.inputFileNames = inputFileNames;
        this.inputFileColumns = inputFileColumns;
    }

    public void run() {
        Table[] inputTables = new Table[inputFileNames.length];

        for (int i = 0; i < inputTables.length; i++) {
            inputTables[i] = Table.getTable(inputFileNames[i], inputFileColumns[i]);
        }

        this.tool.run(inputTables, parameters);
    }


}
