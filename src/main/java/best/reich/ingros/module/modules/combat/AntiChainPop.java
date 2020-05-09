package best.reich.ingros.module.modules.combat;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.network.PacketEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketEntityStatus;

@ModuleManifest(label = "AntiChainPop", category = ModuleCategory.COMBAT, color = 0xff40AE70)
public class AntiChainPop extends ToggleableModule {

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.POST) {
            if (event.getPacket() instanceof SPacketEntityStatus) {
                final SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
                if (packet.getOpCode() == 35) {
                    final Entity entity = packet.getEntity(mc.world);
                    if (entity.getDisplayName().equals(mc.player.getDisplayName())) {
                        final AutoFeetObby autoFeetObby = (AutoFeetObby) IngrosWare.INSTANCE.moduleManager.getModule("AutoFeetObby");
                        if (!autoFeetObby.isEnabled()) {
                            autoFeetObby.toggle();
                        }
                    }
                }
            }
        }
    }
}