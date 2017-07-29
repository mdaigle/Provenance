import java.util.List;

public class InputTableMetadata {
    public final TableHeader tableHeader;
    public final List<String> rows;

    InputTableMetadata(TableHeader header, List<String> rows) {
        this.tableHeader = header;
        this.rows = rows;
    }

    public boolean dependsOnAllRows() {
        return rows.size() > 0 && rows.get(0).equals("ALL");
    }

    public boolean dependsOnGreaterThanFive() {
        return rows.size() > 0 && rows.get(0).equals(">5");
    }
}
