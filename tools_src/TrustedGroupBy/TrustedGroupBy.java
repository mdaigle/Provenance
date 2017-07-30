import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class TrustedGroupBy {
    public static void main(String[] args) {
        int numParams = Integer.parseInt(args[0]);
        int numInputTables = Integer.parseInt(args[1]);

        ArrayList<String> params = new ArrayList<>();

        int i = 2;
        while (i < 2 + numParams) {
            if (i >= args.length) { throw new RuntimeException("Not enough parameters provided"); }
            params.add(args[i++]);
        }

        ArrayList<String> inputTableIds = new ArrayList<>();
        ArrayList<String> inputTableCsvs = new ArrayList<>();
        while (i < 2 + numParams + numInputTables) {
            if (i >= args.length) { throw new RuntimeException("Not enough input table ids provided"); }
            inputTableIds.add(args[i++]);
            if (i >= args.length) { throw new RuntimeException("Not enough input tables provided"); }
            inputTableCsvs.add(args[i++]);
        }

        TrustedGroupBy(params, inputTableIds, inputTableCsvs);
    }

    public static void TrustedGroupBy(List<String> params, List<String> inputTableIds, List<String> inputTableCsvs) {
        assert(params.size() == 1);
        assert(inputTableIds.size() == 1);
        assert(inputTableCsvs.size() == 1);

        Map<String, Integer> map = new HashMap<>();
        int groupByCol = Integer.parseInt(params.get(0));
        ArrayList<String> outputRows = new ArrayList<>();

        String csv = inputTableCsvs.get(0);
        String[] rows = csv.split(",,");
        for (String row : rows) {
            String[] cols = row.split(",");
            String group = cols[groupByCol];
            Integer count = map.get(group);
            if (count == null) {
                count = 0;
            }
            count += 1;
            map.put(group, count);
        }

        for (String group : map.keySet()) {
            System.out.printf("%s,%d\n", group, map.get(group));
        }

        System.out.println();
        System.out.println(inputTableIds.get(0) + "\tALL");
    }
}
