package best.reich.ingros.module.modules;

import best.reich.ingros.events.network.PacketEvent;
import net.b0at.api.event.types.EventType;
import best.reich.ingros.mixin.accessors.ICPacketTabComplete;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.b0at.api.event.Subscribe;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
@ModuleManifest(label = "AntiTabComplete", category = ModuleCategory.OTHER,hidden = true)
public class  AntiTabComplete extends ToggleableModule {

    public AntiTabComplete() {
        setHidden(true);
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        if(event.getType() == EventType.PRE) {
            if (event.getPacket() instanceof CPacketTabComplete) {
                CPacketTabComplete packet = (CPacketTabComplete) event.getPacket();
                final String message = packet.getMessage();
                if (message.startsWith(".")) {
                    String[] arguments = message.split(" ");
                    ((ICPacketTabComplete) packet).setMessage((arguments[arguments.length - 1]));
                }
            }
        }
    }
}
