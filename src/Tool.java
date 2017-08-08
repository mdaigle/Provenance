import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
    private List<Parameter> parameters;

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
    public Tool(int toolId, String name, int numTables, List<Parameter> parameters) {
        this.toolId = toolId;
        this.name = name;
        this.numTables = numTables;
        this.parameters = parameters;
    }

    /**
     * Runs the tool and returns the output csv and metadata.
     * @param inputTables
     * @param params
     * @return
     */
    public ToolOutput run(List<Table> inputTables, List<Parameter> params, String outputFileName) {
        String fileName = this.getFileName();
        String[] args = this.getArgs(inputTables, params);

        // Build the command
        String[] javaCommand = new String[]{DEFAULT_TOOL_RUNNER, DEFAULT_TOOL_RUNNER_OPTION, fileName};
        //String[] command = Stream.concat(Arrays.stream(javaCommand), Arrays.stream(args)).toArray(String[]::new);

        // Build the process
        ProcessBuilder pb = new ProcessBuilder(javaCommand);
        pb.redirectErrorStream(true);

        // Invoke the process
        RawToolOutput rto = this.invokeProcess(pb, args);

        // Figure out how many columns are in the output csv
        int numOutputCols = rto.csv.split("\n", 2)[0].split(",").length;

        // Build the output metadata
        String outputCsv = rto.csv;
        TableMetadata outputMetadata = TableMetadata.fromCondensed(rto.condensedMetadata, numOutputCols, toolId, params);

        ToolOutput output = new ToolOutput(outputCsv, outputMetadata);

        // write the output to the output file
        Tool.writeOutput(outputFileName, output);

        // Build a table for the output file and add dependencies
        Table outputTable = Table.getTable(outputFileName);
        List<TableHeader> inputTableHeaders = inputTables.stream().map(t -> t.getHeader()).collect(Collectors.toList());
        ProvenanceSystem.getProvenanceManager().addDependencies(outputTable.getHeader(), inputTableHeaders);
        ProvenanceSystem.getProvenanceManager().save();

        return output;
    }

    public static void rerunTool(String outputFileName, TableMetadata tm) {
        List<Table> inputTables = tm.inputTables.stream()
                .map(table -> table.tableHeader)
                .map(Table::getTable)
                .collect(Collectors.toList());

        Tool tool = getById(tm.toolId);
        tool.run(inputTables, tm.parameters, outputFileName);
    }

    /**
     * Get the file name for this tool
     * @return
     */
    public String getFileName() {
        return Main.TOOL_DIR + this.name + DEFAULT_TOOL_FILE_EXTENSION;
    }

    /**
     * Creates an argument string.
     * @param inputTables
     * @param params
     * @return
     */
    private static String[] getArgs(List<Table> inputTables, List<Parameter> params) {
        ArrayList<String> args = new ArrayList<>();

        args.add(String.format("%d", params.size()));
        args.add(String.format("%d", inputTables.size()));

        for (Parameter p : params) {
            args.add(p.getValue());
        }

        for (Table table : inputTables) {
            args.add(table.getName());
            String csv = table.getCSV();
            args.add(csv.replaceAll("\\r?\\n", ",,"));
        }

        return args.toArray(new String[]{});
    }

    /**
     * Invokes the process and splits its raw output up into a csv string and a metadata string.
     * @param pb
     * @return
     */
    private RawToolOutput invokeProcess(ProcessBuilder pb, String[] args) {
        String outputCSV = "";
        String condensedOutputMetadata = "";
        // Run the tool in a separate system process
        try {
            Process proc = pb.start();
            InputStream stdout = proc.getInputStream();
            OutputStream stdin = proc.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

            writer.write(String.join(" ", args));
            writer.flush();
            writer.close();

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

    /**
     * Writes the derived data and its metadata to files.
     * @param outputFileName
     * @param output
     */
    public static void writeOutput(String outputFileName, ToolOutput output) {
        // Write out the csv
        File outFile = new File(Main.DATA_DIR + outputFileName + ".csv");
        File metadataFile = new File(Main.DATA_DIR + outputFileName + ".metadata");
        try {
            // Delete so we don't have to do some janky overwrite thing
            outFile.delete();
            outFile.createNewFile();
            metadataFile.delete();
            metadataFile.createNewFile();
        }
        catch (IOException e) {
            System.out.println("Error creating output file");
            System.out.println(e.getMessage());
        }

        try {
            // Flush so that the csv isn't lost when the object is overwritten
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
            writer.write(output.csv);
            writer.flush();

            writer = new BufferedWriter(new FileWriter(metadataFile));
            writer.write(output.metadata.toJSON());
            writer.flush();

            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to output file");
            System.out.println(e.getMessage());
        }
    }

    /*private String generateMetadataJSON(int numCols, Table[] inputTables, Parameter[] params) {
        String[] inputTableIds = new String[inputTables.length];
        for (int i = 0; i < inputTableIds.length; i++) {
            inputTableIds[i] = inputTables[i].getHash();
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
        return parameters.size();
    }

    public Parameter getParameter(int index) {
        return parameters.get(index);
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public int getToolId() {
        return toolId;
    }

    public static Tool getById(int toolId) {
        return ProvenanceSystem.getDbManager().getToolById(toolId);
    }
}
