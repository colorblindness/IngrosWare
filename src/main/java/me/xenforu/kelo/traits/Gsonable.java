package me.xenforu.kelo.traits;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
public interface Gsonable {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();
}
