package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.other.ClickBlockEvent;
import best.reich.ingros.events.other.DamageBlockEvent;
import best.reich.ingros.events.other.ResetBlockRemovingEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Mode;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;


@ModuleManifest(label = "FastBreak", category = ModuleCategory.OTHER, color = 0xfffED3)
public class FastBreak extends ToggleableModule {
    @Setting("Mode")
    @Mode({"PACKET", "DAMAGE","INSTANCE"})
    public String mode = "PACKET";
    @Setting("Reset")
    public boolean reset = true;
    @Setting("DoubleBreak")
    public boolean doubleBreak = false;

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.world == null || mc.player == null) return;
        setSuffix(mode);
        if (event.getType() == EventType.PRE) {
            mc.playerController.blockHitDelay = 0;
            if (this.reset && mc.gameSettings.keyBindUseItem.isKeyDown()) {
                mc.playerController.isHittingBlock = false;
            }
        }
    }

    @Subscribe
    public void resetBlockDamage(ResetBlockRemovingEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (this.reset) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void clickBlock(ClickBlockEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (this.reset) {
            if (mc.playerController.curBlockDamageMP > 0.1f) {
                mc.playerController.isHittingBlock = true;
            }
        }
    }

    @Subscribe
    public void damageBlock(DamageBlockEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (mc.world.getBlockState(event.getPos()).getBlock() == Blocks.PORTAL) return;
        if (canBreak(event.getPos())) {

            if (this.reset) {
                mc.playerController.isHittingBlock = false;
            }

            switch (this.mode) {
                case "PACKET":
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                    event.setCancelled(true);
                    break;
                case "DAMAGE":
                    if (mc.playerController.curBlockDamageMP >= 0.7f) {
                        mc.playerController.curBlockDamageMP = 1.0f;
                    }
                    break;
                case "INSTANT":
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                    mc.playerController.onPlayerDestroyBlock(event.getPos());
                    mc.world.setBlockToAir(event.getPos());
                    break;
            }
        }

        if (this.doubleBreak) {
            final BlockPos above = event.getPos().add(0, 1, 0);

            if (canBreak(above) && mc.player.getDistance(above.getX(), above.getY(), above.getZ()) <= 5f) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, above, event.getFacing()));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, above, event.getFacing()));
                mc.playerController.onPlayerDestroyBlock(above);
                mc.world.setBlockToAir(above);
            }
        }
    }

    private boolean canBreak(BlockPos pos) {
        final IBlockState blockState = mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();

        return block.getBlockHardness(blockState, mc.world, pos) != -1;
    }

}
