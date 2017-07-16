import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *  Represents a real table in the data-database. Each Table should have a unique name (used as identifier).
 *  TODO: hash id instead or something, because user generated ids suck
 */
public class Table {
    private String id;
    private String name;
    private String csv;
    private TableMetadata tm;

    /**
     * Creates a new base Table.
     * @param name
     */
    public Table(String name, String csv, TableMetadata tm) {
        this.name = name;
        this.csv = csv;
        this.tm = tm;

        //TODO: another way to make a UUID?
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(csv.getBytes(StandardCharsets.UTF_8));
            id = new String(hash, "UTF-8");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            id = null;
        }
    }

    public static String getSubset(String content, String[] columns) {
        int[] columnNums = new int[columns.length];
        for (int i = 0; i < columnNums.length; i++) {
            columnNums[i] = Integer.parseInt(columns[i]);
        }

        return Arrays.stream(content.split(System.lineSeparator()))
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

    public static Table getTable(String fileName, String[] columns) {
        String content = "";
        String metadata = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(Main.DATA_DIR + fileName + ".csv")));
            metadata = new String(Files.readAllBytes(Paths.get(Main.DATA_DIR + fileName + ".metadata")));
        } catch (IOException e) {
        }

        TableMetadata tm = TableMetadata.fromJson(metadata);

        if (columns.length != tm.numCols) {
            content = Table.getSubset(content, columns);
        }

        return new Table(fileName, content, tm);
    }

    public String getId() {
        return this.id;
    }

    /**
     * Returns the table's name.
     * @return the table's name
     */
    public String getName() {
        return name;
    }

    public TableMetadata getMetadata() {
        return tm;
    }

    public String getCSV() {
        return csv;
    }
}
