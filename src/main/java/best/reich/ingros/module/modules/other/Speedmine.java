package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.mixin.accessors.IPlayerControllerMP;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;


@ModuleManifest(label = "Speedmine", category = ModuleCategory.OTHER, color = 0xFF5AEEAF)
public class Speedmine extends ToggleableModule {
    @Override
    public void onDisable() {
        mc.player.removePotionEffect(MobEffects.HASTE);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (event.getType() == EventType.PRE) {
            ((IPlayerControllerMP)mc.playerController).setBlockHitDelay(0);
            boolean item = mc.player.getHeldItem(EnumHand.MAIN_HAND) == ItemStack.EMPTY;
            mc.player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 100, item ? 1 : 0));
        }
    }
}