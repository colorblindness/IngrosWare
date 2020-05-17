package best.reich.ingros.module.modules.player;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.network.PacketEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;


@ModuleManifest(label = "NoBreakAnimation", category = ModuleCategory.PLAYER, color = 0xFAEEAF,hidden = true)
public class NoBreakAnimation extends ToggleableModule {
    private boolean isMining = false;
    private BlockPos lastPos = null;
    private EnumFacing lastFacing = null;

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.PRE) {
            if (event.getPacket() instanceof CPacketPlayerDigging) {
                final CPacketPlayerDigging cPacketPlayerDigging = (CPacketPlayerDigging) event.getPacket();
                if (mc.world.getBlockState(cPacketPlayerDigging.getPosition()).getBlock() == Blocks.PORTAL) return;
                for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(cPacketPlayerDigging.getPosition()))) {
                    if (entity instanceof EntityEnderCrystal) {
                        this.resetMining();
                        return;
                    } else if (entity instanceof EntityLivingBase) {
                        this.resetMining();
                        return;
                    }
                }
                if (cPacketPlayerDigging.getAction().equals(CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                    this.isMining = true;
                    this.setMiningInfo(cPacketPlayerDigging.getPosition(), cPacketPlayerDigging.getFacing());
                }
                if (cPacketPlayerDigging.getAction().equals(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                    this.resetMining();
                }
            }
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;
        if (!mc.gameSettings.keyBindAttack.isKeyDown()) {
            this.resetMining();
            return;
        }
        if (this.isMining && this.lastPos != null && this.lastFacing != null) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing));
        }
    }

    private void setMiningInfo(final BlockPos lastPos, final EnumFacing lastFacing) {
        this.lastPos = lastPos;
        this.lastFacing = lastFacing;
    }

    public void resetMining() {
        this.isMining = false;
        this.lastPos = null;
        this.lastFacing = null;
    }
}
