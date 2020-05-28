package best.reich.ingros;

import best.reich.ingros.manager.*;
import best.reich.ingros.notification.NotificationManager;
import best.reich.ingros.util.thealtening.AltService;
import me.xenforu.kelo.Client;
import me.xenforu.kelo.setting.SettingManager;
import net.b0at.api.event.Event;
import net.b0at.api.event.EventManager;

import java.nio.file.Files;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
public class IngrosWare extends Client {
    public static IngrosWare INSTANCE = new IngrosWare();
    public final EventManager<Event> bus = new EventManager<>(Event.class);
    public SettingManager settingManager = new SettingManager();
    public FriendManager friendManager = new FriendManager();
    public ModuleManager moduleManager = new ModuleManager();
    public CommandManager commandManager = new CommandManager();
    public MacroManager macroManager = new MacroManager();
    public NotificationManager notificationManager = new NotificationManager();
    public ProfileManager profileManager = new ProfileManager();
    private final AltService altService = new AltService();
    public IngrosWare() {
        super("IngrosWare", "4.1", new String[]{"x3", "carroteater", "Xenforu"});
    }

    @Override
    public void load() {
        friendManager.load();
        moduleManager.load();
        commandManager.load();
        macroManager.load();
        System.out.println("[IngrosWare] Loaded!");
    }

    @Override
    public void unload() {
        if(!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        friendManager.unload();
        moduleManager.unload();
        profileManager.unload();
    }
    public void switchToMojang() {
        try {
            altService.switchService(AltService.EnumAltService.MOJANG);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
    }
    public void switchToTheAltening() {
        try {
            altService.switchService(AltService.EnumAltService.THEALTENING);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
    }
}
