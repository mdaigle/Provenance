import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        ProvenanceManager pm = new ProvenanceManager();
        pm.initializeDb();

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
                    listDependencies(pm);
                    break;
                case 4:
                    createTool(s, pm);
                    break;
                case 5:
                    createTable(s, pm);
                    break;
                case 6:
                    createDerivedTable(s, pm);
                    break;
                case 7:
                    revokeAccessToTable(s, pm);
            }
        }
    }

    private static void listTools(ProvenanceManager pm) {
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

    private static void listTables(ProvenanceManager pm) {
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

    private static void listDependencies(ProvenanceManager pm) {
        for (Table t : pm.getTables()) {
            System.out.printf("%s: ", t.getName());

            Set<Table> dependencies = pm.getDependencies(t);

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

    private static void createTool(Scanner s, ProvenanceManager pm) {
        System.out.print("Enter name: ");
        String name = s.next();
        System.out.print("Enter number of input tables: ");
        int numTables = s.nextInt();

        Tool t = new Tool(name, numTables);

        pm.addTool(t);

        System.out.printf("\nCreated a new tool with name: %s\n\n", name);
    }

    private static void createTable(Scanner s, ProvenanceManager pm) {
        System.out.print("Enter table name: ");
        String tableName = s.next();

        Table base = new Table(tableName);
        pm.addTable(base);

        System.out.printf("\nCreated a new table with name: %s\n\n", tableName);
    }

    private static void createDerivedTable(Scanner s, ProvenanceManager pm) {
        System.out.println("Enter tool name: ");
        String toolName = s.next();

        Tool tool = null;
        for (Tool t : pm.getTools()) {
            if (t.getName().equals(toolName)) {
                tool = t;
            }
        }

        if (tool == null) {
            System.out.println("Invalid tool name");
            return;
        }

        int numTables = tool.getNumTables();
        Table[] tables = new Table[numTables];

        for (int i = 0; i < numTables; i++ ) {
            System.out.printf("Enter table %d name: ", i+1);
            String tableName = s.next();

            tables[i] = null;
            for (Table t : pm.getTables()) {
                if (t.getName().equals(tableName)) {
                    tables[i] = t;
                }
            }

            if (tables[i] == null) {
                System.out.println("Invalid table name");
                return;
            }
        }

        ToolInstance ti = new ToolInstance(tool, tables);

        System.out.println("Enter derived table name: ");
        String derivedName = s.next();

        Table t = new Table(derivedName, ti);
        pm.addTable(t, ti);

        System.out.println("Created a derived table");
    }

    private static void revokeAccessToTable(Scanner s, ProvenanceManager pm) {
        System.out.print("Enter name of table to revoke: ");
        String name = s.next();

        Table table = null;
        for (Table t : pm.getTables()) {
            if (t.getName().equals(name)) {
                table = t;
            }
        }

        if (table == null) {
            System.out.println("Invalid table name");
            return;
        }

        pm.revokeAccess(table);
    }
}