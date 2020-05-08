package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.mixin.accessors.IPlayerSP;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;

@ModuleManifest(label = "PortalGui", category = ModuleCategory.OTHER, color = 0xfFf4F500,hidden = true)
public class PortalGui extends ToggleableModule {

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.PRE) {
            ((IPlayerSP)mc.player).setInPortal(false);
        }
    }
}
