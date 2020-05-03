package best.reich.ingros.manager;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.macro.Macro;
import com.google.gson.reflect.TypeToken;
import me.xenforu.kelo.manager.impl.MapManager;
import me.xenforu.kelo.traits.Gsonable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
public class MacroManager extends MapManager<String, Macro> implements Gsonable {
    private File macroFile;

    @Override
    public void load() {
        macroFile = new File(IngrosWare.INSTANCE.path + File.separator + "macros.json");
        try {
            if (!macroFile.exists()) {
                macroFile.createNewFile();
                saveFile();
                return;
            }
            loadFile();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void unload() {
        saveFile();
    }

    private void saveFile() {
        if (macroFile.exists()) {
            try (PrintWriter writer = new PrintWriter(macroFile)) {
                writer.print(GSON.toJson(getMap()));
            } catch (Exception ignored) {
            }
        }
    }

    private void loadFile() {
        try (FileReader inFile = new FileReader(macroFile)) {
            setMap(GSON.fromJson(inFile, new TypeToken<Map<String, Macro>>() {
            }.getType()));
            if (getMap() == null) setMap(new HashMap<>());
        } catch (Exception ignored) {
        }
    }

    public void addMacro(String label, int key,String text) {
        put(label.toLowerCase(), new Macro(label, key, text));
    }

    public Macro getMacro(String label) {
        return getMap().get(label.toLowerCase());
    }

    public File getMacroFile() {
        return macroFile;
    }

    public boolean isMacro(String label) {
        return getMacro(label) != null;
    }

    public Macro getMacroByKey(int key) {
        for (Macro macro : getValues()) {
            if (macro.getKey() == key) {
                return macro;
            }
        }
        return null;
    }
}
