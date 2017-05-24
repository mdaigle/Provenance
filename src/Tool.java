import java.io.*;

/**
 * Created by mdaigle on 4/5/17.
 */
public class Tool {
    private static final String DEFAULT_TOOL_FILE_EXTENSION = ".jar";
    private static final String DEFAULT_TOOL_START_COMMAND = "java -jar";

    /**
     * The id of this tool.
     */
    private int toolId;

    /**
     * The name of this tool.
     */
    private String name;

    /**
     * The number of tables this tool takes as input.
     */
    private int numTables;

    /**
     * Creates a new Tool with given name and expected number of input tables.
     * @param name
     * @param numTables
     */
    public Tool(String name, int numTables) {
        this.name = name;
        this.numTables = numTables;
    }

    public String run(Table[] inputTables, Parameter[] params) {
        String metadata = this.generateMetadata(inputTables, params);
        String[] CSVs = new String[inputTables.length];
        for (int i = 0; i < inputTables.length; i++) {
            CSVs[i] = inputTables[i].getCSV();
        }

        String startCommand = DEFAULT_TOOL_START_COMMAND;
        String fileName = this.name + DEFAULT_TOOL_FILE_EXTENSION;
        String args = String.join(" ", CSVs);

        String fullCommand = startCommand + " " + fileName + " " + args;

        System.out.println(fullCommand);

        String outputCSV = "";
        // Run the tool in a separate system process
        try {
            Process proc = Runtime.getRuntime().exec(fullCommand);

            InputStream in = proc.getInputStream();
            InputStream err = proc.getErrorStream();
            OutputStream out = proc.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();

            String line;
            do {
                line = reader.readLine();
                builder.append(line != null ? line : "");
            }
            while (line != null);

            outputCSV = builder.toString();


        } catch (IOException e) {
            System.out.println("Error running tool");
        }

        return outputCSV;
    }

    private String generateMetadata(Table[] inputTables, Parameter[] params) {
        String[] inputTableIds = new String[inputTables.length];
        for (int i = 0; i < inputTableIds.length; i++) {
            inputTableIds[i] = inputTables[i].getId();
        }
        TableMetadata metadata = new TableMetadata(this.toolId, inputTableIds, params);
        return metadata.toJSON();
    }

    /**
     * Returns the name of this tool.
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the number of tables this tool takes as input.
     * @return
     */
    public int getNumTables() {
        return this.numTables;
    }
}
