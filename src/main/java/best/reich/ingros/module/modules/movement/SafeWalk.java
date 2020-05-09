package best.reich.ingros.module.modules.movement;

import best.reich.ingros.events.other.SafeWalkEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
@ModuleManifest(label = "Safewalk", category = ModuleCategory.MOVEMENT, color = 0x1F85DE)
public class SafeWalk extends ToggleableModule {

    @Subscribe
    public void onSafewalk(SafeWalkEvent event) {
        if (mc.world == null || mc.player == null) return;
        event.setCancelled(true);
    }

}
