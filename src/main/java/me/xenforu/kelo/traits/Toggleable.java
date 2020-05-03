package me.xenforu.kelo.traits;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public interface Toggleable {
    boolean getState();
    void setEnabled(boolean enabled);
    void onEnable();
    void onDisable();
    void toggle();
}
