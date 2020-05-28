package best.reich.ingros.module.modules.render;

import best.reich.ingros.events.render.RenderChatBackgroundEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;

@ModuleManifest(label = "ClearChat", category = ModuleCategory.RENDER, color = 0xFF05BAEC, hidden = true)
public class ClearChat extends ToggleableModule {

    @Subscribe
    public void onChatRender(RenderChatBackgroundEvent event) {
        event.setCancelled(true);
    }

}
