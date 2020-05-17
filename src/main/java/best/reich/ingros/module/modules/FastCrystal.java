package best.reich.ingros.module.modules.combat;

import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.mixin.accessors.IMinecraft;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.minecraft.init.Items;

@ModuleManifest(label = "FastCrystal", category = ModuleCategory.COMBAT, color = 0xff40AE70)
public class FastCrystal extends ToggleableModule {

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        if (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            ((IMinecraft)mc).setRightClickDelayTimer(0);
        }
    }
}
