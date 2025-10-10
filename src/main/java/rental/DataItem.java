package rental;

public abstract class DataItem {
    protected String id;

    public DataItem() {}
    public DataItem(String id){this.id = id;}

    public abstract String getId();
}
