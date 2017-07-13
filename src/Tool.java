import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Tool {
    private static final String DEFAULT_TOOL_FILE_EXTENSION = ".jar";
    private static final String DEFAULT_TOOL_RUNNER = "java";
    private static final String DEFAULT_TOOL_RUNNER_OPTION = "-jar";

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
    private int numParams;
    private List<Parameter.ParameterType> paramTypes;

    public class ToolOutput {
        public String csv;
        public TableMetadata metadata;

        public ToolOutput(String csv, TableMetadata metadata) {
            this.csv = csv;
            this.metadata = metadata;
        }
    }

    /**
     * The raw output of a tool.
     * Includes a csv string and a metadata string, the two components of a tool output.
     */
    private class RawToolOutput {
        String csv;
        String condensedMetadata;

        RawToolOutput(String csv, String condensedMetadata) {
            this.csv = csv;
            this.condensedMetadata = condensedMetadata;
        }
    }

    /**
     * Creates a new Tool with given name and expected number of input tables.
     * @param name
     * @param numTables
     */
    public Tool(int toolId, String name, int numTables, int numParams, List<Parameter.ParameterType> paramTypes) {
        this.toolId = toolId;
        this.name = name;
        this.numTables = numTables;
        this.numParams = numParams;
        this.paramTypes = paramTypes;
    }

    /**
     * Runs the tool and returns the output csv and metadata.
     * @param inputTables
     * @param params
     * @return
     */
    public ToolOutput run(Table[] inputTables, Parameter[] params) {
        String fileName = this.getFileName();
        String[] args = this.getArgs(inputTables, params);

        // Build the command
        String[] javaCommand = new String[]{DEFAULT_TOOL_RUNNER, DEFAULT_TOOL_RUNNER_OPTION, fileName};
        String[] command = Stream.concat(Arrays.stream(javaCommand), Arrays.stream(args)).toArray(String[]::new);

        // Build the process
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        // Invoke the process
        RawToolOutput rto = this.invokeProcess(pb);

        // Figure out how many columns are in the output csv
        int numOutputCols = rto.csv.split("\n", 2)[0].split(",").length;

        // Build the output metadata
        String outputCsv = rto.csv;
        TableMetadata outputMetadata = TableMetadata.fromCondensed(rto.condensedMetadata, numOutputCols, toolId, params);

        return new ToolOutput(outputCsv, outputMetadata);
    }

    /**
     * Get the file name for this tool
     * @return
     */
    private String getFileName() {
        return Main.BASE_DIR + this.name + DEFAULT_TOOL_FILE_EXTENSION;
    }

    /**
     * Creates an argument string.
     * @param inputTables
     * @param params
     * @return
     */
    private static String[] getArgs(Table[] inputTables, Parameter[] params) {
        ArrayList<String> args = new ArrayList<>();

        args.add(String.format("%d", params.length));
        args.add(String.format("%d", inputTables.length));

        for (Parameter p : params) {
            args.add(p.getValue());
        }

        for (int i = 0; i < inputTables.length; i++) {
            args.add(inputTables[i].getId());
            args.add(inputTables[i].getCSV());
        }

        return args.toArray(new String[]{});
    }

    /**
     * Invokes the process and splits its raw output up into a csv string and a metadata string.
     * @param pb
     * @return
     */
    private RawToolOutput invokeProcess(ProcessBuilder pb) {
        String outputCSV = "";
        String condensedOutputMetadata = "";
        // Run the tool in a separate system process
        try {
            Process proc = pb.start();
            InputStream in = proc.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            outputCSV = this.readOutputBlock(reader);
            condensedOutputMetadata = this.readOutputBlock(reader);
        } catch (IOException e) {
            System.out.println("Error running tool");
            System.out.println(e.getMessage());
        }

        return new RawToolOutput(outputCSV, condensedOutputMetadata);
    }

    /**
     * Reads a block of lines from a buffered reader. Terminates at a null or empty line.
     * @param reader
     * @return
     * @throws IOException
     */
    private String readOutputBlock(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();

        // Read the csv output
        String line = reader.readLine();
        while (line != null && !line.equals("")) {
            builder.append(line);
            builder.append("\n");
            line = reader.readLine();
        }

        return builder.toString();
    }

    /*private String generateMetadataJSON(int numCols, Table[] inputTables, Parameter[] params) {
        String[] inputTableIds = new String[inputTables.length];
        for (int i = 0; i < inputTableIds.length; i++) {
            inputTableIds[i] = inputTables[i].getId();
        }
        TableMetadata metadata = new TableMetadata(numCols, this.toolId, inputTableIds, params);
        return metadata.toJSON();
    }*/

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

    public int getNumParams() {
        return numParams;
    }

    public List<Parameter.ParameterType> getParamTypes() {
        return paramTypes;
    }

    public int getToolId() {
        return toolId;
    }
}
