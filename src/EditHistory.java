import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EditHistory implements Iterable<EditHistory.Edit> {

    private TableHeader tableHeader;
    private List<Edit> edits;

    @Override
    public Iterator<Edit> iterator() {
        return edits.iterator();
    }

    public enum Operation {
        ABORT,
        STOP,
        ADD,
        DELETE,
        UPDATE;

        public static boolean isValidOp(String opName) {
            try {
                valueOf(opName);
                return true;
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }
    }

    public static class Edit {
        private final Operation operation;
        private final int lineNumber;

        public Edit(Operation operation, int lineNumber) {
            this.operation = operation;
            this.lineNumber = lineNumber;
        }

        public Operation getOperation() {
            return operation;
        }

        public int getLineNumber() {
            return lineNumber;
        }
    }

    public EditHistory(TableHeader tableHeader) {
        this.tableHeader = tableHeader;
        edits = new ArrayList<>();
    }

    public void addEdit(Operation operation, int lineNumber) {
        addEdit(new Edit(operation, lineNumber));
    }

    public void addEdit(Edit edit) {
        edits.add(edit);
    }

    public Edit getEdit(int index) {
        return edits.get(index);
    }

    public TableHeader getTableHeader() {
        return tableHeader;
    }

    public int size() {
        return edits.size();
    }
}
