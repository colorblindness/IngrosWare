package me.xenforu.kelo.command;

import me.xenforu.kelo.manager.impl.MapManager;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public abstract class AbstractCommandManager extends MapManager<String, Command> {

    protected void register(Command command) {
        put(command.getLabel(), command);
    }

}
