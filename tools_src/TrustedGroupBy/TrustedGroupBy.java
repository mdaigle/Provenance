import java.util.*;
import java.util.stream.Collectors;

public class TrustedGroupBy {
    public static void main(String[] a) {
        Scanner scanner = new Scanner(System.in);
        List<String> args = new ArrayList<>();
        boolean noInput = true;
        while (scanner.hasNext() || noInput) {
            noInput = false;
            args.add(scanner.next());
        }

        int numParams = Integer.parseInt(args.get(0));
        int numInputTables = Integer.parseInt(args.get(1));

        ArrayList<String> params = new ArrayList<>();

        int i = 2;
        while (i < 2 + numParams) {
            if (i >= args.size()) { throw new RuntimeException("Not enough parameters provided"); }
            params.add(args.get(i++));
        }

        ArrayList<String> inputTableIds = new ArrayList<>();
        ArrayList<String> inputTableCsvs = new ArrayList<>();
        while (i < 2 + numParams + numInputTables) {
            if (i >= args.size()) { throw new RuntimeException("Not enough input table ids provided"); }
            inputTableIds.add(args.get(i++));
            if (i >= args.size()) { throw new RuntimeException("Not enough input tables provided"); }
            inputTableCsvs.add(args.get(i++));
        }

        TrustedGroupBy(params, inputTableIds, inputTableCsvs);
    }

    public static void TrustedGroupBy(List<String> params, List<String> inputTableIds, List<String> inputTableCsvs) {
        assert(params.size() == 1);
        assert(inputTableIds.size() == 1);
        assert(inputTableCsvs.size() == 1);

        Map<String, Group> map = new HashMap<>();
        int groupByCol = Integer.parseInt(params.get(0));

        String csv = inputTableCsvs.get(0);
        String[] rows = csv.split(",,");
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i];
            String[] cols = row.split(",");
            String key = cols[groupByCol];
            Group group = map.get(key);
            if (group == null) {
                group = new Group();
            }
            group.addRow(i);
            map.put(key, group);
        }

        for (String key : map.keySet()) {
            Group group = map.get(key);
            String prov = group.getRows().stream().map(Object::toString).collect(Collectors.joining("/"));
            int count = group.getCount();
            System.out.printf("%s,%d,%s\n", key, count, prov);
        }

        System.out.println();
        System.out.println(inputTableIds.get(0) + "\tALL");
    }

    private static class Group {
        private int count = 0;
        private List<Integer> rows = new ArrayList<>();

        void addRow(int rowNum) {
            count++;
            rows.add(rowNum);
        }

        int getCount() {
            return count;
        }

        List<Integer> getRows() {
            return rows;
        }
    }
}
