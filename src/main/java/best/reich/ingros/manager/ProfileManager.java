package best.reich.ingros.manager;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.xenforu.kelo.manager.impl.MapManager;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.UUID;

public class ProfileManager extends MapManager<String, UUID> {

    private static final String NAME = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String PROFILE = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    @Override
    public void load() { }

    @Override
    public void unload() {
        getMap().clear();
    }

    public UUID getUUID(final String name) {
        if (getMap().containsKey(name)) {
            return getMap().get(name);
        }
        try {
            final Reader uuidReader = new InputStreamReader(
                    new URL(String.format(NAME, name)).openStream());
            final JsonObject jsonObject = new JsonParser().parse(uuidReader).getAsJsonObject();
            String unfomatted = jsonObject.get("id").getAsString();
            String formatted = "";
            for (final int length : new int[] { 8, 4, 4, 4, 12 }) {
                formatted += "-";
                for (int i = 0; i < length; ++i) {
                    formatted += unfomatted.charAt(0);
                    unfomatted = unfomatted.substring(1);
                }
            }
            formatted = formatted.substring(1);
            final UUID uuid = UUID.fromString(formatted);
            getMap().put(name, uuid);
            return uuid;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public String getName(final UUID uuid) {
        try {
            if (getMap().containsValue(uuid)) {
                return getMap().entrySet().stream().filter(entry -> uuid == entry.getValue())
                        .findFirst().get().getKey();
            }
        } catch (Exception ex) {
        }
        try {
            final Reader uuidReader = new InputStreamReader(
                    new URL(String.format(PROFILE,
                            uuid.toString().replaceAll("-", ""))).openStream());
            final JsonObject jsonObject = new JsonParser().parse(uuidReader).getAsJsonObject();
            final String name = jsonObject.get("name").getAsString();
            getMap().put(name, uuid);
            return name;
        } catch (Exception exception) {
            exception.printStackTrace();
            return "";
        }
    }
}
