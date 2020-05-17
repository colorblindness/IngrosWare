package best.reich.ingros.module.modules.render;

import best.reich.ingros.events.render.BossbarEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;

@ModuleManifest(label = "NoBossBar", category = ModuleCategory.RENDER, color = 0xFF505E41,hidden = true)
public class NoBossBar extends ToggleableModule {

    @Subscribe
    public void onBossBar(BossbarEvent event) {
        event.setCancelled(true);
    }
}
