package best.reich.ingros.module.modules.player;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.events.other.ClickBlockEvent;
import net.b0at.api.event.types.EventType;
import best.reich.ingros.mixin.accessors.ICPacketPlayer;
import best.reich.ingros.mixin.accessors.IPlayerControllerMP;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.b0at.api.event.Subscribe;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
@ModuleManifest(label = "AutoMine", category = ModuleCategory.PLAYER, color = 0x1FDEDB)
public class AutoMine extends ToggleableModule {
    private BlockPos pos;

    @Subscribe
    public void onClickBlock(ClickBlockEvent event) {
        pos = event.getPos();
    }

    @Subscribe
    public void onPacketSend(PacketEvent event) {
        if (mc.player == null || pos == null) return;
        if (event.getType() == EventType.PRE) {
            if (event.getPacket() instanceof CPacketPlayer) {
                final ICPacketPlayer icPacketPlayer = (ICPacketPlayer) event.getPacket();
                if (mc.player.getDistanceSq(pos) > 25) {
                    mc.playerController.onPlayerDamageBlock(new BlockPos(0, 0, 0), EnumFacing.UP);
                    return;
                }
                final float[] rotations = getBlockRotations(pos.getX(), pos.getY(), pos.getZ());
                icPacketPlayer.setYaw(rotations[0]);
                icPacketPlayer.setPitch(rotations[1]);
            }
        }
    }

    @Subscribe
    public void onPostUpdate(UpdateEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.POST) {
            if (pos == null) {
                return;
            }

            if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR)
                pos = null;

            if (mc.player.getDistanceSq(pos) > 25) {
                mc.playerController.onPlayerDamageBlock(new BlockPos(0, 0, 0), EnumFacing.UP);
                return;
            }
            ((IPlayerControllerMP) mc.playerController).setBlockHitDelay(0);
            final ItemStack currentItem = mc.player.inventory.getCurrentItem();
            int oldDamage = 0;
            if (!currentItem.isEmpty()) {
                oldDamage = currentItem.getItemDamage();
            }
            mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            if (!currentItem.isEmpty()) {
                currentItem.setItemDamage(oldDamage);
            }
        }
    }

    @Override
    public void onDisable() {
        pos = null;
        mc.playerController.resetBlockRemoving();
    }

    private float[] getBlockRotations(final double x, final double y, final double z) {
        final double var4 = x - mc.player.posX + 0.5;
        final double var5 = z - mc.player.posZ + 0.5;
        final double var6 = y - (mc.player.posY + mc.player.getEyeHeight() - 1.0);
        final double var7 = MathHelper.sqrt(var4 * var4 + var5 * var5);
        final float var8 = (float) (Math.atan2(var5, var4) * 180.0 / 3.141592653589793) - 90.0f;
        return new float[]{var8, (float) (-(Math.atan2(var6, var7) * 180.0 / 3.141592653589793))};
    }


}
