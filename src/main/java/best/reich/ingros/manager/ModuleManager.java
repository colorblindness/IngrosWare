package best.reich.ingros.manager;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.module.persistent.Commands;
import best.reich.ingros.module.persistent.Keybinds;
import best.reich.ingros.module.persistent.Overlay;
import best.reich.ingros.util.ClassUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.xenforu.kelo.module.IModule;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.manage.AbstractModuleManager;
import me.xenforu.kelo.module.type.ToggleableModule;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
public class ModuleManager extends AbstractModuleManager {

    @Override
    public void load() {
        register(new Commands());
        register(new Keybinds());
        register(new Overlay());
        loadInternalModules();
        loadExternalModules();
        getValues().forEach(IModule::init);
        load(new File(IngrosWare.INSTANCE.path.toFile(), "modules").toPath());
    }

    @Override
    public void unload() {
        save(new File(IngrosWare.INSTANCE.path.toFile(), "modules").toPath());
    }

    private String getJarFolder() {
        // get name and path
        String name = getClass().getName().replace('.', '/');
        name = getClass().getResource("/" + name + ".class").toString();
        // remove junk
        name = name.substring(0, name.indexOf(".jar") + 4);
        name = name.substring(name.lastIndexOf(':')-1).replace('%', ' ');
        String s = "";
        for (int k=0; k<name.length(); k++) {
            s += name.charAt(k);
            if (name.charAt(k) == ' ') k += 2;
        }
        return s.replace('/', File.separatorChar);
    }

    private void loadInternalModules() {
        try {
            System.out.println("DIR: " + getJarFolder());
            if (ClassUtil.getClassesIn(getJarFolder()).isEmpty()) System.out.println("[IngrosWare] No internal modules found!");
            for (Class clazz : ClassUtil.getClassesIn(getJarFolder())) {
                if (clazz != null && ToggleableModule.class.isAssignableFrom(clazz)) {
                    final ToggleableModule module = (ToggleableModule) clazz.newInstance();
                    register(module);
                    System.out.println("[IngrosWare] Found internal module " + module.getLabel());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadExternalModules() {
        try {
            final File dir = new File(IngrosWare.INSTANCE.path + File.separator + "externals" + File.separator + "modules");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (ClassUtil.getClassesEx(dir.getPath()).isEmpty()) System.out.println("[IngrosWare] No external modules found!");
            for (Class clazz : ClassUtil.getClassesEx(dir.getPath())) {
                if (clazz != null && ToggleableModule.class.isAssignableFrom(clazz)) {
                    final ToggleableModule module = (ToggleableModule) clazz.newInstance();
                    register(module);
                    System.out.println("[IngrosWare] Found external module " + module.getLabel());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(Path source) {
        getValues().forEach(plugin -> {
            Path pluginConfiguration = new File(source.toFile(), plugin.getLabel().toLowerCase() + ".json").toPath();
            if (Files.exists(source) && Files.exists(pluginConfiguration)) {
                try (Reader reader = new FileReader(pluginConfiguration.toFile())) {
                    JsonElement element = new JsonParser().parse(reader);
                    if (element.isJsonObject()) {
                        plugin.load(element.getAsJsonObject());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void save(Path destination) {
        if (!Files.exists(destination)) {
            try {
                Files.createDirectory(destination);
            } catch (IOException ignored) { }
        }
        File[] configurations = destination.toFile().listFiles();
        if (!Files.exists(destination)) {
            try {
                Files.createDirectory(destination);
            } catch (IOException ignored) { }
        } else if (configurations != null) {
            for (File configuration : configurations) {
                configuration.delete();
            }
        }

        getValues().forEach(plugin -> {
            Path pluginConfiguration = new File(destination.toFile(), plugin.getLabel().toLowerCase() + ".json").toPath();
            JsonObject object = new JsonObject();
            plugin.save(object);
            if (!object.entrySet().isEmpty()) {
                try {
                    Files.createFile(pluginConfiguration);
                } catch (IOException e) {
                    return;
                }
                try (Writer writer = new FileWriter(pluginConfiguration.toFile())) {
                    writer.write(new GsonBuilder()
                            .setPrettyPrinting()
                            .create()
                            .toJson(object));
                } catch (IOException ignored) { }
            }
        });
        configurations = destination.toFile().listFiles();
        if (configurations == null || configurations.length == 0) {
            try {
                Files.delete(destination);
            } catch (IOException ignored) {}
        }
    }

    public ArrayList<IModule> getModulesFromCategory(ModuleCategory moduleCategory) {
        final ArrayList<IModule> iModules = new ArrayList<>();
        for (IModule iModule : getValues()) {
            if (iModule.getCategory() == moduleCategory) iModules.add(iModule);
        }
        return iModules;
    }
}
