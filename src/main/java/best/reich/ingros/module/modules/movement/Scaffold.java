package best.reich.ingros.module.modules.movement;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.other.SafeWalkEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.util.math.TimerUtil;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@ModuleManifest(label = "Scaffold", category = ModuleCategory.MOVEMENT, color = 0xffAfA033)
public class Scaffold extends ToggleableModule {
    private TimerUtil timerMotion = new TimerUtil();

    @Subscribe
    public void onUpdate(UpdateEvent event) {
         if (event.getType() == EventType.POST) {
            BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
            int slot = mc.player.inventory.currentItem;
            for (int i = 8; i >= 0; i--) {
                if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
                    ItemBlock iBlock = (ItemBlock) mc.player.inventory.getStackInSlot(i).getItem();
                    if (isBlockAbleToBeWalkedOn(iBlock.getBlock().getDefaultState())) {
                        for (EnumFacing facing : EnumFacing.values()) {
                            if (facing == EnumFacing.UP) {
                                continue;
                            }
                            if (isBlockAbleToBeWalkedOn(mc.world.getBlockState(pos.offset(facing)))) {
                                mc.getConnection().sendPacket(new CPacketHeldItemChange(i));
                                mc.player.inventory.currentItem = i;
                                lookAtBlockSide(pos.offset(facing), facing);
                                place(pos.offset(facing), facing);
                                mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
                                mc.player.inventory.currentItem = slot;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    public void onSafewalk(SafeWalkEvent event) {
        event.setCancelled(true);
    }

    private void lookAtBlockSide(BlockPos pos, EnumFacing facing) {
        if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getMaterial() == Material.AIR) {
            double d0 = pos.getX() + 0.5 + (float) facing.getOpposite().getFrontOffsetX() / 2 - mc.player.posX;
            double d1 = pos.getY() + 0.5 + (float) facing.getOpposite().getFrontOffsetY() / 2 - (mc.player.posY + (double) mc.player.getEyeHeight());
            double d2 = pos.getZ() + 0.5 + (float) facing.getOpposite().getFrontOffsetZ() / 2 - mc.player.posZ;
            final float[] rots = getRotations(d0, d2, d1);
            mc.getConnection().sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], true));
        }
    }

    public static float[] getRotations(double xDiff, double zDiff, double yDiff) {
        final double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
        final float yaw = (float) MathHelper.wrapDegrees((MathHelper.atan2(zDiff, xDiff) * 57.29577951308232D) - 90.0D);
        final float pitch = (float) MathHelper.wrapDegrees((-(MathHelper.atan2(yDiff, dist) * 57.29577951308232D)));
        return new float[]{yaw, pitch};
    }

    private boolean isBlockAbleToBeWalkedOn(IBlockState state) {
        AxisAlignedBB bb = state.getCollisionBoundingBox(mc.world, new BlockPos(0, 0, 0));
        return bb != null && bb.equals(Block.FULL_BLOCK_AABB);
    }

    private void place(BlockPos pos, EnumFacing facing) {
        if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getMaterial() == Material.AIR) {
            if (mc.world.getBlockState(pos).getMaterial() != Material.AIR) {
                ItemStack stack = mc.player.getHeldItem(EnumHand.MAIN_HAND);
                int i = stack.getCount();
                EnumActionResult enumactionresult = mc.playerController.processRightClickBlock(mc.player, mc.world, pos, facing.getOpposite(), new Vec3d(0, 0, 0), EnumHand.MAIN_HAND);
                if (enumactionresult == EnumActionResult.SUCCESS) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    if (!stack.isEmpty() && (stack.getCount() != i || mc.playerController.isInCreativeMode())) {
                        mc.entityRenderer.itemRenderer.resetEquippedProgress(EnumHand.MAIN_HAND);
                    }
                }
            }
        }
    }
}
