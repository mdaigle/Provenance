import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        } catch (NoSuchAlgorithmException nsae) {
            id = null;
        } catch (UnsupportedEncodingException uce) {
            id = null;
        }
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

    /*public ArrayList<Tuple> getRows() {
        return ProvenanceSystem.getDbManager().getAllRows(name);
    }*/

    /*public void addRows(ArrayList<Tuple> rows) {
        for (Tuple t : rows) {
            this.addRow(t);
        }
    }*/

    /*public void addRow(Tuple t) {
        ProvenanceSystem.getDbManager().addRow(this, t);
    }*/

    public String getCSV() {
        return csv;
    }
}
