import java.io.*;
import java.util.Arrays;
import java.util.stream.Stream;

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
    public Tool(int toolId, String name, int numTables) {
        this.toolId = toolId;
        this.name = name;
        this.numTables = numTables;
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

        String[] javaCommand = new String[]{"java", "-jar", fileName};
        String[] command = Stream.concat(Arrays.stream(javaCommand), Arrays.stream(args)).toArray(String[]::new);


        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        RawToolOutput rto = this.invokeProcess(pb);

        String outputCsv = rto.csv;
        TableMetadata outputMetadata = TableMetadata.fromCondensed(rto.condensedMetadata, toolId, params);

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
     * @param getParams
     * @return
     */
    private String[] getArgs(Table[] inputTables, Parameter[] getParams) {
        String[] args = new String[inputTables.length * 2];
        for (int i = 0; i < inputTables.length; i++) {
            args[2*i] = inputTables[i].getId();
            args[2*i + 1] = inputTables[i].getCSV();
        }

        //TODO: add parameters to argument array somehow

        return args;
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
}
