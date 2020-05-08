package best.reich.ingros.module.modules.player;

import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.mixin.accessors.IMinecraft;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.minecraft.init.Items;


@ModuleManifest(label = "FastEXP", category = ModuleCategory.PLAYER, color = 0x66ff66,hidden = true)
public class FastEXP extends ToggleableModule {


    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.player != null && (mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE || mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE)) {
            new Thread(() -> ((IMinecraft)mc).setRightClickDelayTimer(0)).start();
        }
    }
}
