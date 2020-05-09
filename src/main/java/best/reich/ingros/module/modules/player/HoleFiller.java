package best.reich.ingros.module.modules.player;

import best.reich.ingros.events.entity.UpdateEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@ModuleManifest(label = "HoleFiller", category = ModuleCategory.PLAYER, color = 0xffAEffAE)
public class HoleFiller extends ToggleableModule {
    @Setting("Fade")
    public boolean fade = true;
    @Setting("Radius")
    @Clamp(maximum = "5")
    public int radius = 5;
    public final List<Hole> holes = new ArrayList<>();
    private final List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) {
            return;
        }
        this.holes.clear();
        final Vec3i playerPos = new Vec3i(mc.player.posX, mc.player.posY, mc.player.posZ);
        for (int x = playerPos.getX() - this.radius; x < playerPos.getX() + this.radius; ++x) {
            for (int z = playerPos.getZ() - this.radius; z < playerPos.getZ() + this.radius; ++z) {
                for (int y = playerPos.getY(); y > playerPos.getY() - 4; --y) {
                    final BlockPos blockPos = new BlockPos(x, y, z);
                    final IBlockState blockState = mc.world.getBlockState(blockPos);
                    if (this.isBlockValid(blockState, blockPos)) {
                        final IBlockState downBlockState = mc.world.getBlockState(blockPos.down());
                        if (downBlockState.getBlock() == Blocks.AIR) {
                            final BlockPos downPos = blockPos.down();
                            if (this.isBlockValid(downBlockState, downPos)) {
                                this.holes.add(new Hole(downPos.getX(), downPos.getY(), downPos.getZ(), true));
                            }
                        } else {
                            this.holes.add(new Hole(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                        }
                    }
                }
            }
        }
        if (!holes.isEmpty()) {
            if (!mc.player.getHeldItemMainhand().isEmpty() && mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemMainhand().getItem()).getBlock() == Blocks.OBSIDIAN) {
                holes.sort(Comparator.comparingDouble(hole1 -> mc.player.getDistance(hole1.getX(), hole1.getY(), hole1.getZ())));
                Hole hole = holes.get(0);
                placeBlock(event, new BlockPos(hole.getX(), hole.getY(), hole.getZ()));
                if (hole.isTall()) placeBlock(event, new BlockPos(hole.getX(), hole.getY() + 1, hole.getZ()));
            }
        }
    }

    private static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
                mc.player.rotationYaw
                        + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
                mc.player.rotationPitch + MathHelper
                        .wrapDegrees(pitch - mc.player.rotationPitch)};
    }

    private static Vec3d getEyesPos() {
        return new Vec3d(mc.player.posX,
                mc.player.posY + mc.player.getEyeHeight(),
                mc.player.posZ);
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = getLegitRotations(vec);

        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround));
    }

    private boolean placeBlock(UpdateEvent event, final BlockPos pos) {
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return false;
        }
        if (!checkForNeighbours(pos)) {
            return false;
        }
        final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
                final Vec3d hitVec = new Vec3d(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (eyesPos.distanceTo(hitVec) <= radius) {
                    final Block neighborPos = mc.world.getBlockState(neighbor).getBlock();
                    if (blackList.contains(neighborPos) || neighborPos == Blocks.SILVER_SHULKER_BOX || neighborPos == Blocks.BLACK_SHULKER_BOX || neighborPos == Blocks.BLUE_SHULKER_BOX || neighborPos == Blocks.BROWN_SHULKER_BOX || neighborPos == Blocks.CYAN_SHULKER_BOX || neighborPos == Blocks.GRAY_SHULKER_BOX || neighborPos == Blocks.GREEN_SHULKER_BOX || neighborPos == Blocks.LIME_SHULKER_BOX || neighborPos == Blocks.LIGHT_BLUE_SHULKER_BOX || neighborPos == Blocks.RED_SHULKER_BOX || neighborPos == Blocks.ORANGE_SHULKER_BOX || neighborPos == Blocks.WHITE_SHULKER_BOX || neighborPos == Blocks.YELLOW_SHULKER_BOX || neighborPos == Blocks.PINK_SHULKER_BOX || neighborPos == Blocks.MAGENTA_SHULKER_BOX || neighborPos == Blocks.PURPLE_SHULKER_BOX) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    }
                    faceVectorPacketInstant(hitVec);
                    mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean checkForNeighbours(final BlockPos blockPos) {
        if (!hasNeighbour(blockPos)) {
            for (final EnumFacing side : EnumFacing.values()) {
                final BlockPos neighbour = blockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private static boolean hasNeighbour(final BlockPos blockPos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = blockPos.offset(side);
            if (!mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
                return true;
            }
        }
        return false;
    }


    private boolean isBlockValid(final IBlockState blockState, final BlockPos blockPos) {
        if (this.holes.contains(blockPos)) {
            return false;
        }
        if (blockState.getBlock() != Blocks.AIR) {
            return false;
        }
        if (mc.player.getDistanceSq(blockPos) < 1.0) {
            return false;
        }
        if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR) {
            return false;
        }
        if (mc.world.getBlockState(blockPos.up(2)).getBlock() != Blocks.AIR) {
            return false;
        }
        final BlockPos[] touchingBlocks = {blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west()};
        int validHorizontalBlocks = 0;
        for (final BlockPos touching : touchingBlocks) {
            final IBlockState touchingState = mc.world.getBlockState(touching);
            if (touchingState.getBlock() != Blocks.AIR && touchingState.isFullBlock()) {
                ++validHorizontalBlocks;
            }
        }
        return validHorizontalBlocks >= 4;
    }

    private class Hole extends Vec3i {
        private boolean tall;

        Hole(final int x, final int y, final int z) {
            super(x, y, z);
        }

        Hole(final int x, final int y, final int z, final boolean tall) {
            super(x, y, z);
            this.tall = true;
        }

        public boolean isTall() {
            return this.tall;
        }

        public void setTall(final boolean tall) {
            this.tall = tall;
        }
    }
}
