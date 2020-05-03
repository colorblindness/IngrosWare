package me.xenforu.kelo;

import me.xenforu.kelo.traits.Labelable;
import me.xenforu.kelo.traits.Loadable;
import me.xenforu.kelo.traits.unLoadable;

import java.io.File;
import java.nio.file.Path;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public abstract class Client implements Loadable, unLoadable, Labelable {
    private String label, version;
    private String[] authors;

    public Path path;

    public Client(String label, String version, String[] authors) {
        this.label = label;
        this.version = version;
        this.authors = authors;

        this.path = new File(System.getProperty("user.home"), label).toPath();
    }

    @Override
    public String getLabel() {
        return label;
    }

    public String getVersion() {
        return version;
    }

    public String[] getAuthors() {
        return authors;
    }
}
