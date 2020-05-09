package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.render.HurtcamEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;

@ModuleManifest(label = "NoHurtcam", category = ModuleCategory.OTHER, color = 0x3F6f6E,hidden = true)
public class NoHurtcam extends ToggleableModule {

    @Subscribe
    public void onHurtCam(HurtcamEvent event) {
        event.setCancelled(true);
    }
}
