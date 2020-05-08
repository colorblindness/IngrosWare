package best.reich.ingros.module.modules.render;

import best.reich.ingros.events.render.RenderArmorEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;

@ModuleManifest(label = "AntiArmorRender", category = ModuleCategory.RENDER, color = 0xff4AE0AE)
public class AntiArmorRender extends ToggleableModule {

    @Subscribe
    public void onArmorRender(RenderArmorEvent event) {
        event.setCancelled(true);
    }
}
