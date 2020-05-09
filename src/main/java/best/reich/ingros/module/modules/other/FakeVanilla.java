package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.network.PacketEvent;
import net.b0at.api.event.types.EventType;
import best.reich.ingros.mixin.accessors.ICPacketCustomPayload;
import io.netty.buffer.Unpooled;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.b0at.api.event.Subscribe;


@ModuleManifest(label = "FakeVanilla", category = ModuleCategory.OTHER,hidden = true)
public class FakeVanilla extends ToggleableModule {

    public FakeVanilla() {
        setHidden(true);
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        if(event.getType() == EventType.POST) {
            if (event.getPacket() instanceof FMLProxyPacket && !mc.isSingleplayer()) {
                event.setCancelled(true);
            }
            if (event.getPacket() instanceof CPacketCustomPayload) {
                CPacketCustomPayload packet = (CPacketCustomPayload)event.getPacket();
                if (packet.getChannelName().equals("MC|Brand")) {
                    ((ICPacketCustomPayload) packet).setData(new PacketBuffer(Unpooled.buffer()).writeString("vanilla"));
                }
            }
        }
    }
}
