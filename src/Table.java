import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  Represents a real table in the data-database. Each Table should have a unique name (used as identifier).
 *  TODO: hash id instead or something, because user generated ids suck
 */
public class Table {
    private TableHeader header;
    private String csv;
    private TableMetadata tm;

    /**
     * Creates a new base Table.
     * @param name
     */
    public Table(String name, String csv, TableMetadata tm) {
        header = new TableHeader();
        header.setName(name);
        this.csv = csv;
        this.tm = tm;

        //TODO: another way to make a UUID?
        String hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashArray = digest.digest(csv.getBytes(StandardCharsets.UTF_8));
            hash = new String(hashArray, "UTF-8");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            hash = null;
        }
        header.setHash(hash);
    }

    public static String getSubset(String content, List<String> columns) {
        List<Integer> columnNums = new ArrayList<>();
        for (String column : columns) {
            columnNums.add(Integer.parseInt(column));
        }

        return Arrays.stream(content.split(System.lineSeparator()))
                .map(line -> getLineSubset(line, columnNums))
                .collect(Collectors.joining(",,"));
    }

    private static String getLineSubset(String line, List<Integer> columns) {
        List<String> values = Arrays.asList(line.split(","));
        List<String> desiredValues = new ArrayList<>();
        for (Integer column : columns) {
            desiredValues.add(values.get(column));
        }
        return String.join(",", desiredValues);
    }

    public boolean impactedBy(EditHistory editHistory) {
        return tm.impactedBy(editHistory);
    }

    public static Table getTable(TableHeader tableHeader) {
        return getTable(tableHeader.getName());
    }

    public static Table getTable(String filename) {
        return getTable(filename, null);
    }

    public static Table getTable(String fileName, List<String> columns) {
        String content;
        String metadata;
        try {
            content = new String(Files.readAllBytes(Paths.get(Main.DATA_DIR + fileName + ".csv")));
            metadata = new String(Files.readAllBytes(Paths.get(Main.DATA_DIR + fileName + ".metadata")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }

        TableMetadata tm = TableMetadata.fromJson(metadata);

        if (columns != null && columns.size() != tm.numCols) {
            content = Table.getSubset(content, columns);
        }

        return new Table(fileName, content, tm);
    }

    public TableHeader getTableHeader() {
        return header;
    }

    public String getHash() {
        return header.getHash();
    }

    /**
     * Returns the table's name.
     * @return the table's name
     */
    public String getName() {
        return header.getName();
    }

    public TableMetadata getMetadata() {
        return tm;
    }

    public String getCSV() {
        return csv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Table table = (Table) o;

        return header.equals(table.header);
    }

    @Override
    public int hashCode() {
        return header.hashCode();
    }
}
