package best.reich.ingros.module.modules.player;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.MotionEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;

@ModuleManifest(label = "AntiVoid", category = ModuleCategory.PLAYER, color = 0xff400f70)
public class AntiVoid extends ToggleableModule {
    @Subscribe
    public void onMotion(MotionEvent event) {
        if (canMoveBack()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 99, mc.player.posZ, false));
        }
    }

    private boolean canMoveBack() {
        return mc.player.fallDistance > 3
                && !IngrosWare.INSTANCE.moduleManager.getModule("Flight").isEnabled()
                && !isBlockUnder();
    }

    private boolean isBlockUnder() {
        if (mc.player.posY < 0)
            return false;
        for (int off = 0; off < (int) mc.player.posY + 2; off += 2) {
            final AxisAlignedBB bb = mc.player.getEntityBoundingBox().offset(0, -off, 0);
            if (!mc.world.getCollisionBoxes(mc.player, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}