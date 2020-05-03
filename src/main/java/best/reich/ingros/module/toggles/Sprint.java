package best.reich.ingros.module.toggles;

import best.reich.ingros.events.entity.UpdateEvent;
import net.b0at.api.event.types.EventType;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.b0at.api.event.Subscribe;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
@ModuleManifest(label = "Sprint", category = ModuleCategory.MOVEMENT, color = 0xff00ff00)
public class Sprint extends ToggleableModule {

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if(event.getType() == EventType.PRE) {
            if (isSprintable(mc.player))
                mc.player.setSprinting(true);
        }
    }

    private boolean isSprintable(EntityPlayerSP player) {
        return !player.isSprinting() && !player.isSneaking() && player.movementInput.moveForward > 0.0f &&
                (player.getFoodStats().getFoodLevel() >= 6f || player.capabilities.allowFlying) &&
                !player.isPotionActive(MobEffects.BLINDNESS);
    }

}
