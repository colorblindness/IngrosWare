package best.reich.ingros.module.modules.movement;

import best.reich.ingros.events.entity.UpdateEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;


@ModuleManifest(label = "NoPush", category = ModuleCategory.MOVEMENT, color = 0xfFDEDB,hidden = true)
public class NoPush extends ToggleableModule {

    private float savedReduction;
        @Subscribe
    public void onUpdate(UpdateEvent event) {
        mc.player.entityCollisionReduction = 1.0F;
    }

    @Override
    public void onEnable() {
        savedReduction = mc.player != null ? mc.player.entityCollisionReduction : 0.0f;
    }

    @Override
    public void onDisable() {
        mc.player.entityCollisionReduction = savedReduction;
    }
}
