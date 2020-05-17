package best.reich.ingros.module.modules.player;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.network.PacketEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.client.CPacketUseEntity;


@ModuleManifest(label = "AntiFriendHit", category = ModuleCategory.OTHER, color = 0xff00ffff)
public class AntiFriendHit extends ToggleableModule {

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (event.getType() == EventType.PRE && event.getPacket() instanceof CPacketUseEntity) {
            final CPacketUseEntity useEntityPacket = (CPacketUseEntity) event.getPacket();
            if (useEntityPacket.getAction() == CPacketUseEntity.Action.ATTACK && mc.world.getEntityByID(useEntityPacket.entityId) != null && IngrosWare.INSTANCE.friendManager.isFriend(mc.world.getEntityByID(useEntityPacket.entityId).getName())) {
                event.setCancelled(true);
            }
        }
    }
}
