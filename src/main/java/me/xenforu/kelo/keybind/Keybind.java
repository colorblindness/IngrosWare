package me.xenforu.kelo.keybind;

import me.xenforu.kelo.keybind.action.IAction;
import me.xenforu.kelo.traits.Labelable;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public class Keybind implements Labelable {
    private String label, key;
    private IAction action;

    public Keybind(String label, String key, IAction action) {
        this.label = label;
        this.key = key;
        this.action = action;
    }

    public void execute() {
        action.execute();
    }

    @Override
    public String getLabel() {
        return label;
    }
}
