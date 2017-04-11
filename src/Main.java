import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        ProvenanceManager pm = new ProvenanceManager();

        while(true) {
            Scanner s = new Scanner(System.in);
            System.out.print("Enter an option: ");
            int option = s.nextInt();

            System.out.println();

            switch (option) {
                case 0:
                    System.exit(0);
                    break;
                case 1:
                    listTools(pm);
                    break;
                case 2:
                    listTables(pm);
                    break;
                case 3:
                    createTool(s, pm);
                    break;
                case 4:
                    createTable(s, pm);
                    break;
                case 5:
                    createDerivedTable(s, pm);
                    break;
            }
        }
    }

    public static void listTools(ProvenanceManager pm) {
        Set<Tool> tools = pm.getTools();

        if (tools.isEmpty()) {
            System.out.println("No tools\n");
            return;
        }

        System.out.println("Tools:");

        for (Tool t : tools) {
            System.out.printf("%s (%d inputs)", t.getName(), t.getNumTables());
        }

        System.out.println("\n");
    }

    public static void listTables(ProvenanceManager pm) {
        Set<Table> tables = pm.getTables();

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

    public static void createTool(Scanner s, ProvenanceManager pm) {
        System.out.print("Enter name: ");
        String name = s.next();
        System.out.print("Enter number of input tables: ");
        int numTables = s.nextInt();

        Tool t = new Tool(name, numTables);

        pm.addTool(t);

        System.out.printf("\nCreated a new tool with name: %s\n\n", name);
    }

    public static void createTable(Scanner s, ProvenanceManager pm) {
        System.out.print("Enter table name: ");
        String tableName = s.next();

        Table base = new Table(tableName, pm);

        System.out.printf("\nCreated a new table with name: %s\n\n", tableName);
    }

    public static void createDerivedTable(Scanner s, ProvenanceManager pm) {
        

    }
}