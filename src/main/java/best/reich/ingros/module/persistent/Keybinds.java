package best.reich.ingros.module.persistent;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.other.KeyPressedEvent;
import best.reich.ingros.gui.clickgui.ClickGUI;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.PersistentModule;
import net.b0at.api.event.Subscribe;
import org.lwjgl.input.Keyboard;

@ModuleManifest(label = "Keybinds", category = ModuleCategory.OTHER)
public class Keybinds extends PersistentModule {

    @Subscribe
    public void onKey(KeyPressedEvent event) {
        IngrosWare.INSTANCE.macroManager.getMap().values().forEach(macro -> {
            if (macro.getKey() == event.getKey()) {
                mc.player.sendChatMessage(macro.getText());
            }
        });

        IngrosWare.INSTANCE.moduleManager.getToggles().forEach(module -> {
            if(module.getBind() == event.getKey()) module.toggle();
        });
    }
}
