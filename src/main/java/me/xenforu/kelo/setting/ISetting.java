package me.xenforu.kelo.setting;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public interface ISetting<V> {
    V getValue();
    void setValue(V value);
    void setValue(String value);
}
