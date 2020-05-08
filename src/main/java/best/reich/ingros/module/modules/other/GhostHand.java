package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.other.BlockCollideEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;


@ModuleManifest(label = "GhostHand", category = ModuleCategory.OTHER, color = 0xff0000AE)
public class GhostHand extends ToggleableModule {


    @Subscribe
    public void canCollide(BlockCollideEvent event) {
        event.setCancelled(true);
    }
}
