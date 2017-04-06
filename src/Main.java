import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ProvenanceManager pm = new ProvenanceManager();

        Table t1 = new Table("first", 3);
        Table t2 = new Table("second", 3);
        Table t3 = new Table("third", 3);

        ArrayList<Column> cols = new ArrayList<>();
        cols.add(t1.getCol(0));
        cols.add(t2.getCol(1));
        cols.add(t3.getCol(2));

        Tool tool1 = new Tool(cols);
        Tool tool2 = new Tool(new Table[] {t1, t2, t3});

        Table t4 = new Table("derived1", 3, tool1);
        Table t5 = new Table("derived2", 3, tool2);


    }


}