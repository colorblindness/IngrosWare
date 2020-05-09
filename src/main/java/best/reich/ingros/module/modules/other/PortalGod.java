package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.network.PacketEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.client.CPacketConfirmTeleport;

@ModuleManifest(label = "PortalGod", category = ModuleCategory.OTHER, color = 0xffAEff33)
public class PortalGod extends ToggleableModule {
    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getType() == EventType.PRE) {
            if (event.getPacket() instanceof CPacketConfirmTeleport)
                event.setCancelled(true);
        }
    }
}
