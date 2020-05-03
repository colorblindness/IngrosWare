package me.xenforu.kelo.keybind;

import me.xenforu.kelo.manager.impl.MapManager;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public abstract class AbstractKeybindManager extends MapManager<String, Keybind> {

    protected void register(Keybind keybind) {
        put(keybind.getLabel(), keybind);
    }

}
