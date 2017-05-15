import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Arrays;
import java.util.Iterator;
/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 */
public class Tuple implements Serializable {
    private static final long serialVersionUID = 1L;
    private Field[] fields;
    private Schema s;
    /**
     * Create a new tuple with the specified schema (type).
     *
     * @param td
     *            the schema of this tuple. It must be a valid TupleDesc
     *            instance with at least one field.
     */
    public Tuple(Schema s) {
        assert (s.size() > 0);
        this.fields = new Field[s.size()];
        for (int i = 0; i < s.size(); i++) {
            this.fields[i] = null;
        }
        this.s = s;
    }
    public Tuple(Schema s, ResultSet r) {
        for (int i = 0; i < s.size(); i++) {
            int type = s.getType(i);
            Field f;
            try {
                switch (type) {
                    case Types.INTEGER:
                        f = new IntField(r.getInt(i));
                        break;
                    case Types.VARCHAR:
                        String val = r.getString(i);
                        f = new StringField(val, val.length());
                        break;
                    default:
                        f = null;
                }
            } catch (Exception e) {
                f = null;
            }
            fields[i] = f;
        }
    }
    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public Schema getSchema() {
        return this.s;
    }

    /**
     * Change the value of the ith field of this tuple.
     *
     * @param i
     *            index of the field to change. It must be a valid index.
     * @param f
     *            new value for the field.
     */
    public void setField(int i, Field f) {
        assert (i >= 0);
        assert (i < this.fields.length);
        fields[i] = f;
    }
    /**
     * @return the value of the ith field, or null if it has not been set.
     *
     * @param i
     *            field index to return. Must be a valid index.
     */
    public Field getField(int i) {
        assert (i >= 0);
        assert (i < this.fields.length);
        return fields[i];
    }
    /**
     * Returns the contents of this Tuple as a string. Note that to pass the
     * system tests, the format needs to be as follows:
     *
     * column1\tcolumn2\tcolumn3\t...\tcolumnN
     *
     * where \t is any whitespace (except a newline)
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(fields[0]);
        for (int i = 1; i < fields.length; i++) {
            builder.append("\t");
            builder.append(fields[i]);
        }
        return builder.toString();
    }
    /**
     * @return
     *        An iterator which iterates over all the fields of this tuple
     * */
    public Iterator<Field> fields()
    {
        return Arrays.asList(fields).iterator();
    }
    /**
     * reset the TupleDesc of this tuple
     * */
    public void resetTupleDesc(TupleDesc td)
    {
        // Shrink fields if necessary
        if (this.td.numFields() > td.numFields()) {
            this.fields = Arrays.copyOfRange(this.fields, td.numFields(), this.td.numFields());
        }
        // TODO: set to null?
        this.td = td;
    }
}