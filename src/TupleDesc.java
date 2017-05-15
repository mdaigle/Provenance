import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {
    private ArrayList<TDItem> tdItems;
    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * The type of the field
         * */
        public final int fieldType;

        /**
         * The name of the field
         * */
        public final String fieldName;
        public TDItem(int type, String n) {
            this.fieldName = n;
            this.fieldType = type;
        }
        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }
    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        return tdItems.iterator();
    }
    private static final long serialVersionUID = 1L;
    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     *
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(int[] typeAr, String[] fieldAr) {
        assert (typeAr.length > 0);
        assert (typeAr.length == fieldAr.length);
        this.tdItems = new ArrayList<TDItem>();
        for (int i = 0; i < typeAr.length; i++){
            TDItem newItem = new TDItem(typeAr[i], fieldAr[i]);
            tdItems.add(newItem);
        }
    }
    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     *
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(int[] typeAr) {
        this(typeAr, new String[typeAr.length]);
    }
    /**
     * Constructor. Create a new tuple desc given a list of TDItems.
     *
     * @param tdItems
     *            array list containing the TDDescription's TDItems. It must
     *            contain at least one entry.
     */
    private TupleDesc(ArrayList<TDItem> tdItems) {
        assert (tdItems.size() > 0);
        this.tdItems = tdItems;
    }
    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        return tdItems.size();
    }
    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     *
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        if (i < 0 || i > tdItems.size()) {
            throw new NoSuchElementException();
        }
        return tdItems.get(i).fieldName;
    }
    /**
     * Gets the type of the ith field of this TupleDesc.
     *
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public int getFieldType(int i) throws NoSuchElementException {
        if (i < 0 || i > tdItems.size()) {
            throw new NoSuchElementException();
        }
        return tdItems.get(i).fieldType;
    }
    /**
     * Find the index of the field with a given name.
     *
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        if (name == null) {
            throw new NoSuchElementException();
        }
        for (int i = 0; i < tdItems.size(); i++) {
            String currName = tdItems.get(i).fieldName;
            if (currName == null) {
                continue;
            }
            // Standard case
            if (tdItems.get(i).fieldName.equals(name)) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     *
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        ArrayList<TDItem> mergedItems = new ArrayList<>(td1.tdItems);
        mergedItems.addAll(td2.tdItems);
        return new TupleDesc(mergedItems);
    }
    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they have the same number of items
     * and if the i-th type in this TupleDesc is equal to the i-th type in o
     * for every i.
     *
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        if (!(o instanceof TupleDesc)) {
            return false;
        }
        TupleDesc oAsTupleDesc = (TupleDesc) o;

        // Check for type mismatches
        for (int i = 0; i < this.tdItems.size(); i++) {
            assert (i < this.tdItems.size());
            assert (i < oAsTupleDesc.tdItems.size());
            int thisType = this.tdItems.get(i).fieldType;
            int otherType = oAsTupleDesc.tdItems.get(i).fieldType;
            if (otherType != thisType) {
                return false;
            }
        }
        return true;
    }
    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        throw new UnsupportedOperationException("unimplemented");
    }
    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     *
     * @return String describing this descriptor.
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int fieldType = tdItems.get(0).fieldType;
        String fieldName = tdItems.get(0).fieldName;
        builder.append(fieldType + "(" + fieldName + ")");
        for (int i = 1; i < tdItems.size(); i++) {
            fieldType = tdItems.get(i).fieldType;
            fieldName = tdItems.get(i).fieldName;
            builder.append(",");
            builder.append(fieldType + "(" + fieldName + ")");
        }
        return builder.toString();
    }
}