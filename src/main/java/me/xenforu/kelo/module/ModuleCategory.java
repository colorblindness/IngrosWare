package me.xenforu.kelo.module;

import me.xenforu.kelo.traits.Labelable;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public enum ModuleCategory implements Labelable {
    COMBAT("Combat"),
    OTHER("Other"),
    PLAYER("Player"),
    MOVEMENT("Movement"),
    RENDER("Render");

    String label;

    ModuleCategory(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
