package best.reich.ingros.module.modules.movement;

import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.mixin.accessors.ISPacketPosLook;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

@ModuleManifest(label = "NoRotate", category = ModuleCategory.OTHER, color = 0xfff33f00,hidden = true)
public class NoRotate extends ToggleableModule {

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getType() == EventType.POST) {
            if (event.getPacket() instanceof SPacketPlayerPosLook && mc.player != null) {
                SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
                ((ISPacketPosLook) packet).setYaw(mc.player.rotationYaw);
                ((ISPacketPosLook) packet).setPitch(mc.player.rotationPitch);
            }
        }
    }
}
