package me.xenforu.kelo.command;

import me.xenforu.kelo.command.annotation.CommandManifest;
import me.xenforu.kelo.traits.Labelable;
import me.xenforu.kelo.traits.Minecraftable;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public abstract class Command implements Labelable, Minecraftable {
    private String label, description;
    private String[] handles;

    public Command() {
        if(getClass().isAnnotationPresent(CommandManifest.class)) {
            CommandManifest commandManifest = getClass().getAnnotation(CommandManifest.class);
            this.label = commandManifest.label();
            this.description = commandManifest.description();
            this.handles = commandManifest.handles();
        }
    }

    public abstract void execute(String[] args);

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String[] getHandles() {
        return handles;
    }
}
