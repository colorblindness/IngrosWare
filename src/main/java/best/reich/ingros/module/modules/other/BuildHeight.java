package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.network.PacketEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;


@ModuleManifest(label = "BuildHeight", category = ModuleCategory.OTHER, color = 0xFAEEAD,hidden = true)
public class BuildHeight extends ToggleableModule {

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.PRE) {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                final CPacketPlayerTryUseItemOnBlock oldPacket = (CPacketPlayerTryUseItemOnBlock) event.getPacket();
                if (oldPacket.getPos().getY() >= 255) {
                    if (oldPacket.getDirection() == EnumFacing.UP) {
                        BuildHeight.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(oldPacket.getPos(), EnumFacing.DOWN, oldPacket.getHand(), oldPacket.getFacingX(), oldPacket.getFacingY(), oldPacket.getFacingZ()));
                        event.cancel();
                    }
                }
            }
        }
    }
}
