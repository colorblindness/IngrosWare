package best.reich.ingros.manager;

import best.reich.ingros.IngrosWare;
import me.xenforu.kelo.manager.impl.ListManager;
import me.xenforu.kelo.manager.impl.MapManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class CapeManager extends MapManager<String, UUID> {

    @Override
    public void load() {
        IngrosWare.INSTANCE.bus.registerListener(this);
        loadNames();

    }

    @Override
    public void unload() {
        getMap().clear();
        IngrosWare.INSTANCE.bus.deregisterListener(this);
    }

    public void loadNames() {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://pastebin.com/raw/yxdvVitT").openStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                String name = IngrosWare.INSTANCE.profileManager.getName(UUID.fromString(line));
                getMap().put(name, UUID.fromString(line));
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
