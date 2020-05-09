package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.network.PacketEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.client.CPacketCloseWindow;


@ModuleManifest(label = "XCarry", category = ModuleCategory.OTHER, color = 0xFFFAEEAE, hidden = true)
public class XCarry extends ToggleableModule {

    @Override
    public void onDisable() {
        if (mc.world != null && mc.player != null)
            mc.player.connection.sendPacket(new CPacketCloseWindow(mc.player.inventoryContainer.windowId));
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.PRE) {
            if (event.getPacket() instanceof CPacketCloseWindow) {
                event.setCancelled(true);
            }
        }
    }
}
