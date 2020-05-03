package me.xenforu.kelo.module;

import com.google.gson.JsonObject;
import me.xenforu.kelo.traits.Labelable;
import me.xenforu.kelo.traits.Minecraftable;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public interface IModule extends Labelable, Minecraftable {
    String getLabel();

    ModuleCategory getCategory();

    void init();

    void save(JsonObject destination);

    void load(JsonObject source);

    boolean isEnabled();
}
