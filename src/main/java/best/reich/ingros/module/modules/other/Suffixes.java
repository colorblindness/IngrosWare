package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.network.PacketEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.client.CPacketChatMessage;

@ModuleManifest(label = "Suffixes", category = ModuleCategory.OTHER, color = 0xff33ff33,hidden = true)
public class Suffixes extends ToggleableModule {
    @Setting("Suffix")
    public String suffix = "-Ingros-";
    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.PRE) {
            if (event.getPacket() instanceof CPacketChatMessage) {
                CPacketChatMessage packetChatMessage = (CPacketChatMessage) event.getPacket();
                if (!packetChatMessage.getMessage().startsWith("-") && !packetChatMessage.getMessage().startsWith("/")&& packetChatMessage.getMessage().length() <= 255 - suffix.length() && !packetChatMessage.getMessage().contains(suffix)) {
                    event.setCancelled(true);
                    mc.player.sendChatMessage(packetChatMessage.getMessage() + " " + suffix);
                }
            }
        }
    }
}
