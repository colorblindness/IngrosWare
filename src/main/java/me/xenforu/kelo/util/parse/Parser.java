package me.xenforu.kelo.util.parse;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public abstract class Parser<T> {
    private int index;

    public abstract T parse(String input);

    public abstract boolean canHandleType(Class type);

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}