import java.util.List;
import java.util.ArrayList;
import java.util.*;

public class TrustedFilterEqual {
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

        TrustedFilterEqual(numParams, numInputTables, params, inputTableIds, inputTableCsvs);
    }

    public static void TrustedFilterEqual(int numParams, int numInputTables, List<String> params, List<String> inputTableIds, List<String> inputTableCsvs) {
        assert(numParams == 2);
        assert(numInputTables == 1);
        assert(params.size() == 2);
        assert(inputTableIds.size() == 1);
        assert(inputTableCsvs.size() == 1);

        int filterCol = Integer.parseInt(params.get(0));
        String val = params.get(1);

        ArrayList<String> outputRows = new ArrayList<>();

        String csv = inputTableCsvs.get(0);
        String[] rows = csv.split(",,");
        for (String row : rows) {
            String[] cols = row.split(",");
            if (cols[filterCol].equals(val)) {
                outputRows.add(row);
            }
        }

        for (String row : outputRows) {
            System.out.println(row);
        }

        System.out.println();
        System.out.println(inputTableIds.get(0) + "\tALL");
    }
}
