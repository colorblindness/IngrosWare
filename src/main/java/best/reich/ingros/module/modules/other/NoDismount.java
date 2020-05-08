package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.network.PacketEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.client.CPacketPlayer;


@ModuleManifest(label = "NoDismount", category = ModuleCategory.OTHER, color = 0xff0AE0AE)
public class NoDismount extends ToggleableModule {


    @Subscribe
    public void sendPacket(PacketEvent event) {
        if (event.getType() == EventType.PRE) {
            if (event.getPacket() instanceof CPacketPlayer.Position) {
                event.setCancelled(true);
                final CPacketPlayer.Position packet = (CPacketPlayer.Position) event.getPacket();
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(packet.getX(mc.player.posX), packet.getY(mc.player.posY), packet.getZ(mc.player.posZ), packet.getYaw(mc.player.rotationYaw), packet.getPitch(mc.player.rotationPitch), packet.isOnGround()));
            }
            if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.PositionRotation)) {
                event.setCancelled(true);
            }
        }
    }
}