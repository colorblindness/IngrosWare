package best.reich.ingros.module.modules.combat;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.UpdateEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Setting;
import me.xenforu.kelo.util.math.TimerUtil;
import net.b0at.api.event.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@ModuleManifest(label = "AutoTrap", category = ModuleCategory.COMBAT, color = 0x4BF7FF)
public class AutoTrap extends ToggleableModule {
    @Setting("EndChest")
    public boolean endChest = true;
    @Clamp(maximum = "1000")
    @Setting("Delay")
    public int delay = 10;
    @Clamp(minimum = "0.0", maximum = "7.0")
    @Setting("Range")
    public float range = 5.5f;
    @Clamp(maximum = "10")
    @Setting("BlocksPerTick")
    public int blockPerTick = 4;
    private final Vec3d[] offsetsDefault = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(1.0, 0.0, -1.0),
            new Vec3d(-1.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 1.0), new Vec3d(1.0, 1.0, -1.0),
            new Vec3d(-1.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 1.0), new Vec3d(1.0, 2.0, -1.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 1.0), new Vec3d(-1.0, 3.0, 0.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 0.0),
            new Vec3d(0.0, 4.0, 0.0)
    };
    private EntityPlayer closestTarget;
    private String lastTickTargetName;
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private boolean isSneaking;
    private int offsetStep = 0;
    private boolean firstRun;
    private final TimerUtil timer = new TimerUtil();
    private final List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);

    @Override
    public void onEnable() {
        if (mc.world == null) {
            this.toggle();
            return;
        }
        this.firstRun = true;
        this.playerHotbarSlot = mc.player.inventory.currentItem;
        this.lastHotbarSlot = -1;
    }

    @Override
    public void onDisable() {
        if (mc.world == null) {
            return;
        }
        if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
            mc.player.inventory.currentItem = this.playerHotbarSlot;
        }
        if (this.isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        this.playerHotbarSlot = -1;
        this.lastHotbarSlot = -1;
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) {
            return;
        }
        this.findClosestTarget();
        if (this.closestTarget == null) {
            if (this.firstRun) {
                this.firstRun = false;
            }
            return;
        }
        if (this.firstRun) {
            this.firstRun = false;
            this.lastTickTargetName = this.closestTarget.getName();
        } else if (lastTickTargetName != null && !this.lastTickTargetName.equals(this.closestTarget.getName())) {
            this.lastTickTargetName = this.closestTarget.getName();
            this.offsetStep = 0;
        }
        final List<Vec3d> placeTargets = new ArrayList<>();
        Collections.addAll(placeTargets, this.offsetsDefault);
        int blocksPlaced = 0;
        while (blocksPlaced < this.blockPerTick) {
            if (this.offsetStep >= placeTargets.size()) {
                this.offsetStep = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos(placeTargets.get(this.offsetStep));
            final BlockPos targetPos = new BlockPos(this.closestTarget.getPositionVector()).down().add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            boolean shouldTryToPlace = true;
            if (!mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
                shouldTryToPlace = false;
            }
            for (final Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))) {
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                    shouldTryToPlace = false;
                    break;
                }
            }
            if (shouldTryToPlace && timer.sleep(delay) && this.placeBlock(targetPos)) {
                ++blocksPlaced;
            }
            ++this.offsetStep;
        }
        if (blocksPlaced > 0) {
            if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                mc.player.inventory.currentItem = this.playerHotbarSlot;
                this.lastHotbarSlot = this.playerHotbarSlot;
            }
            if (this.isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.isSneaking = false;
            }
        }
    }

    private boolean placeBlock(final BlockPos pos) {
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
                if (eyesPos.distanceTo(hitVec) <= this.range) {
                    final int slot = this.findBlockInHotbar();
                    if (slot == -1) {
                        return false;
                    }
                    if (this.lastHotbarSlot != slot) {
                        mc.player.inventory.currentItem = slot;
                        this.lastHotbarSlot = slot;
                    }
                    final Block neighborPos = mc.world.getBlockState(neighbor).getBlock();
                    if (blackList.contains(neighborPos) || neighborPos == Blocks.SILVER_SHULKER_BOX || neighborPos == Blocks.BLACK_SHULKER_BOX || neighborPos == Blocks.BLUE_SHULKER_BOX || neighborPos == Blocks.BROWN_SHULKER_BOX || neighborPos == Blocks.CYAN_SHULKER_BOX || neighborPos == Blocks.GRAY_SHULKER_BOX || neighborPos == Blocks.GREEN_SHULKER_BOX || neighborPos == Blocks.LIME_SHULKER_BOX || neighborPos == Blocks.LIGHT_BLUE_SHULKER_BOX || neighborPos == Blocks.RED_SHULKER_BOX || neighborPos == Blocks.ORANGE_SHULKER_BOX || neighborPos == Blocks.WHITE_SHULKER_BOX || neighborPos == Blocks.YELLOW_SHULKER_BOX || neighborPos == Blocks.PINK_SHULKER_BOX || neighborPos == Blocks.MAGENTA_SHULKER_BOX || neighborPos == Blocks.PURPLE_SHULKER_BOX) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                        this.isSneaking = true;
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

    private int findBlockInHotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block instanceof BlockObsidian) {
                    return i;
                }
            }
        }
        if (endChest) {
            for (int i = 0; i < 9; ++i) {
                final ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock) stack.getItem()).getBlock();
                    if (block instanceof BlockEnderChest) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private void findClosestTarget() {
        final List<EntityPlayer> playerList = mc.world.playerEntities;
        this.closestTarget = null;
        for (final EntityPlayer target : playerList) {
            if (target == mc.player || target.getEntityId() == -1488 || IngrosWare.INSTANCE.friendManager.isFriend(target.getName()) || target.getHealth() <= 0.0f)
                continue;
            if (this.closestTarget == null) {
                this.closestTarget = target;
            } else {
                if (mc.player.getDistanceToEntity(target) >= mc.player.getDistanceToEntity(this.closestTarget)) {
                    continue;
                }
                this.closestTarget = target;
            }
        }
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
}