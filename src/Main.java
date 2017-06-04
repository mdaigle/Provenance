import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static final String BASE_DIR = "/Users/mdaigle/Desktop/provenance/";

    public static void main(String[] args) {
        while(true) {
            Scanner s = new Scanner(System.in);
            System.out.print("Enter an option: ");
            int option = s.nextInt();

            switch (option) {
                case 0:
                    System.exit(0);
                    break;
                case 1:
                    listTools();
                    break;
                case 2:
                    listTables();
                    break;
                case 3:
                    runTool(s);
                    break;
            }
        }
    }

    private static void listTools() {
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

    private static void listTables() {
        Collection<Table> tables = ProvenanceSystem.getProvenanceManager().getTables();

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
        for (Table t : ProvenanceSystem.getProvenanceManager().getTables()) {
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
    }

    private static void runTool(Scanner s) {
        // Get tool name, csv names, and a name for the output table
        // TODO: ask for input for parameters too
        System.out.print("Enter tool name: ");
        String toolName = s.next();

        File toolFile = new File(BASE_DIR + toolName + ".jar");
        if (!toolFile.exists()) {
            System.out.println("Invalid tool name");
            return;
        }

        //TODO: read this off the tool entry in the db
        int numTables = 1;
        Table[] inputTables = new Table[numTables];

        for (int i = 0; i < numTables; i++ ) {
            System.out.printf("CSV %d file name: ", i+1);
            String inputFileName = s.next();

            String content = "";
            String metadata = "";
            try {
                content = new String(Files.readAllBytes(Paths.get(BASE_DIR + inputFileName + ".csv")));
                metadata = new String(Files.readAllBytes(Paths.get(BASE_DIR + inputFileName + ".metadata")));
            } catch (IOException e) {
                // Can't find file so ask again
                System.out.println("File or metadata not found");
                i--;
                continue;
            }

            TableMetadata tm = TableMetadata.fromJson(metadata);

            System.out.print("Columns: ");
            String columnsString = s.next();
            String[] columns = columnsString.split(",");

            if (columns.length != tm.numCols) {
                content = getCSVSubset(content, columns);
            }

            Table table = new Table(inputFileName, content, tm);
            inputTables[i] = table;
        }

        System.out.print("Enter name for output table: ");
        String outputFileName = s.next();

        //TODO: this shouldn't be hardcoded. read from database.
        Tool tool = new Tool(1, toolName, numTables);
        Tool.ToolOutput output = tool.run(inputTables, new Parameter[]{});

        // Write out the csv
        File outFile = new File(BASE_DIR + outputFileName + ".csv");
        File metadataFile = new File(BASE_DIR + outputFileName + ".metadata");
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

    private static String getCSVSubset(String csv, String[] columns) {
        int[] columnNums = new int[columns.length];
        for (int i = 0; i < columnNums.length; i++) {
            columnNums[i] = Integer.parseInt(columns[i]);
        }

        return Arrays.stream(csv.split(System.lineSeparator()))
                .map(line -> getLineSubset(line, columnNums))
                .collect(Collectors.joining(",,"));
    }

    private static String getLineSubset(String line, int[] columns) {
        String[] values = line.split(",");
        String[] desiredValues = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            desiredValues[i] = i < columns.length ? values[columns[i]] : "";
        }
        return String.join(",", desiredValues);
    }

    private static void revokeAccessToTable(Scanner s) {
        System.out.print("Enter name of table to revoke: ");
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

        ProvenanceSystem.getProvenanceManager().revokeAccess(table);
    }
}