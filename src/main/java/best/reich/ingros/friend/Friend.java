package best.reich.ingros.friend;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
public class Friend {
    private String name, alias;

    public Friend(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public Friend(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getAlias() {
        return this.alias;
    }
}
