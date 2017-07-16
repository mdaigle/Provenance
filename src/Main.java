import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static final String BASE_DIR = "/Users/mdaigle/Mezuri Research/provenance";
    static final String TOOL_DIR = BASE_DIR + "/tools/";
    static final String DATA_DIR = BASE_DIR + "/data/";
    static final String RUNBOOK_DIR = BASE_DIR + "/runbooks/";

    public static void main(String[] args) {
        ProvenanceSystem.initialize();

        while(true) {
            Scanner s = new Scanner(System.in);
            System.out.print("Enter an option: ");
            int option = s.nextInt();

            switch (option) {
                case 0:
                    System.exit(0);
                    break;
                case 1:
                    //listTools();
                    break;
                case 2:
                    //listTables();
                    break;
                case 3:
                    runTool(s);
                    break;
                case 4:
                    createTool(s);
                    break;
                case 5:
                    updateTool(s);
                    break;
                case 6:
                    readInRunBook(s);
                    break;
            }
        }
    }

    /*private static void listTools() {
        Set<Tool> tools = ProvenanceSystem.getProvenanceManager().getTools();

        if (tools.isEmpty()) {
            System.out.println("No tools\n");
            return;
        }

        System.out.println("Tools:");

        for (Tool t : tools) {
            System.out.printf("%s (%d inputs)\n", t.getName(), t.getNumTables());
        }
    }

    /*private static void listTables() {


        if (tables.isEmpty()) {
            System.out.println("No tables\n");
            return;
        }

        System.out.println("Tables:");

        for (Table t : tables) {
            System.out.println(t.getName());
        }

        System.out.println();
    }

    private static void listDependencies() {
        /*for (Table t : ProvenanceSystem.getProvenanceManager().getTables()) {
            System.out.printf("%s: ", t.getName());

            Set<Table> dependencies = ProvenanceSystem.getProvenanceManager().getDependencies(t);

            if (dependencies == null) {
                System.out.println();
                continue;
            }

            for (Table dependent : dependencies) {
                System.out.printf("%s ", dependent.getName());
            }
            System.out.println();
        }
    }*/

    private static void runTool(Scanner s) {
        // Get tool name, params, csv names, and a name for the output table
        System.out.print("Enter tool id: ");
        String toolId = s.next();

        Tool tool = ProvenanceSystem.getDbManager().getToolById(Integer.parseInt(toolId));

        File toolFile = new File(tool.getFileName());
        if (!toolFile.exists()) {
            System.out.println("Invalid tool name");
            return;
        }

        int numParams = tool.getNumParams();
        Parameter[] params = new Parameter[numParams];
        for (int i = 0; i < numParams; i++) {
            System.out.printf("Param %d: ", i);
            String val = s.next();
            params[i] = new Parameter(tool.getParamTypes().get(i), val);
        }

        int numTables = tool.getNumTables();
        Table[] inputTables = new Table[numTables];

        for (int i = 0; i < numTables; i++ ) {
            System.out.printf("CSV %d file name: ", i+1);
            String inputFileName = s.next();
            System.out.print("Columns: ");
            String columnsString = s.next();

            String[] columns = columnsString.split(",");

            inputTables[i] = Table.getTable(inputFileName, columns);
        }

        System.out.print("Enter name for output table: ");
        String outputFileName = s.next();

        // Run the tool
        Tool.ToolOutput output = tool.run(inputTables, params);

        // Write out the csv
        File outFile = new File(DATA_DIR + outputFileName + ".csv");
        File metadataFile = new File(DATA_DIR + outputFileName + ".metadata");
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

        System.out.println("Tool ran successfully");
    }

    private static void createTool(Scanner s) {
        System.out.print("Enter tool name: ");
        String toolName = s.next();

        System.out.print("Enter num input tables: ");
        Integer numInputTables = s.nextInt();

        System.out.print("Enter num parameters: ");
        Integer numParameters = s.nextInt();

        System.out.print("Enter parameter types: ");
        String parameterTypesString = s.next();

        List<Parameter.ParameterType> parameterTypes;
        try {
            parameterTypes = Arrays.stream(parameterTypesString.split(","))
                    .map(Parameter.ParameterType::valueOf)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal parameter type. Valid types are STRING, INTEGER, and PREDICATE");
            return;
        }

        Tool tool = new Tool(0, toolName, numInputTables, numParameters, parameterTypes);

        ProvenanceSystem.getDbManager().addTool(tool);
    }

    private static void updateTool(Scanner s) {
        System.out.print("Enter tool id: ");
        Integer toolId = s.nextInt();

        Tool old = ProvenanceSystem.getDbManager().getToolById(toolId);
        if (old == null) {
            System.out.printf("Invalid tool id.");
            return;
        }

        System.out.print("Enter tool name: ");
        String toolName = s.next();

        System.out.print("Enter num input tables: ");
        Integer numInputTables = s.nextInt();

        System.out.print("Enter num parameters: ");
        Integer numParameters = s.nextInt();

        System.out.print("Enter parameter types: ");
        String parameterTypesString = s.next();

        List<Parameter.ParameterType> parameterTypes;
        try {
            parameterTypes = Arrays.stream(parameterTypesString.split(","))
                    .map(Parameter.ParameterType::valueOf)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal parameter type. Valid types are STRING, INTEGER, and PREDICATE");
            return;
        }

        Tool tool = new Tool(toolId, toolName, numInputTables, numParameters, parameterTypes);

        ProvenanceSystem.getDbManager().updateTool(tool);
    }

    private static void readInRunBook(Scanner s) {
        System.out.print("Enter runbook name: ");
        String runBookName = s.next();

        try {
            String content = new String(Files.readAllBytes(Paths.get(Main.RUNBOOK_DIR + runBookName + ".runbook")));
            RunBook rb = RunBook.fromJson(content);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void revokeAccessToTable(Scanner s) {
        /*System.out.print("Enter name of table to revoke: ");
        String name = s.next();

        Table table = null;
        for (Table t : ProvenanceSystem.getProvenanceManager().getTables()) {
            if (t.getName().equals(name)) {
                table = t;
            }
        }

        if (table == null) {
            System.out.println("Invalid table name");
            return;
        }

        ProvenanceSystem.getProvenanceManager().revokeAccess(table);*/
    }
}