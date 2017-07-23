public class TableHeader {
    private String hash;
    private String name;

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {

        return hash;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableHeader that = (TableHeader) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        return result;
    }
}
