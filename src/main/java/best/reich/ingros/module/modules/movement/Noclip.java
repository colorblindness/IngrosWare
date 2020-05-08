package best.reich.ingros.module.modules.movement;

import best.reich.ingros.events.entity.UpdateEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;

@ModuleManifest(label = "Noclip", category = ModuleCategory.MOVEMENT, color = 0xff33ff33)
public class Noclip extends ToggleableModule {

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.noClip = false;
        }
    }

    @Subscribe
    public void onLocalPlayerUpdate(UpdateEvent event) {
        mc.player.noClip = true;
        mc.player.onGround = false;
        mc.player.fallDistance = 0;
    }
}
