package best.reich.ingros.module.modules.combat;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.mixin.accessors.IMinecraft;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Mode;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ModuleManifest(label = "SelfTrap", category = ModuleCategory.COMBAT, color = 0xff4AEf70)
public class SelfTrap extends ToggleableModule {
    @Clamp(maximum = "32")
    @Setting("Range")
    public float smartRange = 4.5f;
    @Clamp(minimum = "1", maximum = "23")
    @Setting("BlocksPerTick")
    public int blocksPerTick = 2;
    @Clamp(maximum = "10")
    @Setting("Range")
    public int tickDelay = 2;
    @Setting("Mode")
    @Mode({"TRAP", "BLOCKOVERHEAD"})
    public String mode = "BLOCKOVERHEAD";
    @Setting("Rotate")
    public boolean rotate = true;
    @Setting("Smart")
    public boolean smart = false;
    @Setting("DisableOnPlace")
    public boolean disableOnPlace = false;
    @Setting("DisableCAOnPlace")
    public boolean disableCAOnPlace = false;
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private int delayStep = 0;
    private boolean isSneaking = false;
    private int offsetStep = 0;
    private boolean firstRun;
    private boolean caOn;
    private EntityPlayer closestTarget;
    public static final List<Block> blackList = Arrays.asList(
            Blocks.ENDER_CHEST,
            Blocks.CHEST,
            Blocks.TRAPPED_CHEST,
            Blocks.CRAFTING_TABLE,
            Blocks.ANVIL,
            Blocks.BREWING_STAND,
            Blocks.HOPPER,
            Blocks.DROPPER,
            Blocks.DISPENSER,
            Blocks.TRAPDOOR
    );
    public static final List<Block> shulkerList = Arrays.asList(
            Blocks.WHITE_SHULKER_BOX,
            Blocks.ORANGE_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX,
            Blocks.LIGHT_BLUE_SHULKER_BOX,
            Blocks.YELLOW_SHULKER_BOX,
            Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX,
            Blocks.GRAY_SHULKER_BOX,
            Blocks.SILVER_SHULKER_BOX,
            Blocks.CYAN_SHULKER_BOX,
            Blocks.PURPLE_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX,
            Blocks.GREEN_SHULKER_BOX,
            Blocks.RED_SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX
    );
    @Override
    public void onEnable() {

        if (IngrosWare.INSTANCE.moduleManager.getModule("CrystalAura").isEnabled()) {
            caOn = true;
        }

        if (mc.player == null) {
            this.toggle();
            return;
        }

        firstRun = true;

        // save initial player hand
        playerHotbarSlot = mc.player.inventory.currentItem;
        lastHotbarSlot = -1;

    }

    @Override
    public void onDisable() {

        caOn = false;

        closestTarget = null;

        if (mc.player == null) {
            return;
        }

        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            mc.player.inventory.currentItem = playerHotbarSlot;
        }

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        playerHotbarSlot = -1;
        lastHotbarSlot = -1;
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;
        setSuffix(mode);
        if (smart)
            findClosestTarget();

        if (mc.player == null) {
            return;
        }

        {
        }

        if (!firstRun) {
            if (delayStep < tickDelay) {
                delayStep++;
                return;
            } else {
                delayStep = 0;
            }
        }

        List<Vec3d> placeTargets = new ArrayList<>();

        if (mode.toLowerCase().equals("trap")) {
            Collections.addAll(placeTargets, Offsets.TRAP);
        }

        if (mode.toLowerCase().equals("blockoverhead")) {
            if (getViewYaw() <= 315 && getViewYaw() >= 225)
                Collections.addAll(placeTargets, Offsets.BLOCKOVERHEADFACINGNEGX);
            else if (getViewYaw() < 45 && getViewYaw() > 0 || getViewYaw() > 315 && getViewYaw() < 360)
                Collections.addAll(placeTargets, Offsets.BLOCKOVERHEADFACINGPOSZ);
            else if (getViewYaw() <= 135 && getViewYaw() >= 45)
                Collections.addAll(placeTargets, Offsets.BLOCKOVERHEADFACINGPOSX);
            else if (getViewYaw() < 225 && getViewYaw() > 135)
                Collections.addAll(placeTargets, Offsets.BLOCKOVERHEADFACINGNEGZ);
        }

        int blocksPlaced = 0;

        while (blocksPlaced < blocksPerTick) {

            if (offsetStep >= placeTargets.size()) {
                offsetStep = 0;
                break;
            }

            BlockPos offsetPos = new BlockPos(placeTargets.get(offsetStep));
            BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).down().add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

            if (closestTarget != null && smart) {
                if (isInRange(getClosestTargetPos())) {
                    if (placeBlockInRange(targetPos)) {
                        blocksPlaced++;
                    }
                }
            } else if (!smart) {
                if (placeBlockInRange(targetPos)) {
                    blocksPlaced++;
                }
            }


            offsetStep++;

        }

        if (blocksPlaced > 0) {

            if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
                mc.player.inventory.currentItem = playerHotbarSlot;
                lastHotbarSlot = playerHotbarSlot;
            }

            if (isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }

        }

        Vec3d overHead = new Vec3d(0, 3, 0);
        BlockPos blockOverHead = new BlockPos(mc.player.getPositionVector()).down().add(overHead.x, overHead.y, overHead.z);
        Block block2 = mc.world.getBlockState(blockOverHead).getBlock();

        if (!(block2 instanceof BlockAir) && !(block2 instanceof BlockLiquid) && disableCAOnPlace && caOn) {
            ((CrystalAura) IngrosWare.INSTANCE.moduleManager.getModule("CrystalAura")).toggle();
        }

        if (!(block2 instanceof BlockAir) && !(block2 instanceof BlockLiquid) && disableOnPlace) {
            this.toggle();
        }
    }

    private boolean placeBlockInRange(BlockPos pos) {

        if (caOn && disableCAOnPlace) {
            ((CrystalAura) IngrosWare.INSTANCE.moduleManager.getModule("CrystalAura")).toggle();
        }

        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }

        // check if entity blocks placing
        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                return false;
            }
        }
        EnumFacing side;
        final RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        if (result == null || result.sideHit == null) {
            side = EnumFacing.UP;
        } else {
            side = result.sideHit;
        }
        
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        if (!canBeClicked(neighbour)) {
            return false;
        }

        Vec3d hitVec = new Vec3d(neighbour).add(new Vec3d(0.5f, 0.5f, 0.5f)).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        int obiSlot = findObiInHotbar();

        if (obiSlot == -1) {
            this.toggle();
        }

        if (lastHotbarSlot != obiSlot) {
            mc.player.inventory.currentItem = obiSlot;
            lastHotbarSlot = obiSlot;
        }

        if (!isSneaking && blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        if (rotate) {
            faceVectorPacketInstant(hitVec);
        }

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        ((IMinecraft)mc).setRightClickDelayTimer(0);

        return true;

    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public BlockPos getClosestTargetPos() {
        if (closestTarget != null) {
            return new BlockPos(Math.floor(closestTarget.posX), Math.floor(closestTarget.posY), Math.floor(closestTarget.posZ));
        } else {
            return null;
        }
    }

    public int getViewYaw() {
        return (int) Math.abs(Math.floor(mc.player.rotationYaw * 8.0F / 360.0F));
    }

    private void findClosestTarget() {

        List<EntityPlayer> playerList = mc.world.playerEntities;

        closestTarget = null;

        for (EntityPlayer target : playerList) {

            if (target == mc.player) {
                continue;
            }

            if (IngrosWare.INSTANCE.friendManager.isFriend(target.getName())) {
                continue;
            }

            if (!(target instanceof EntityLivingBase)) {
                continue;
            }

            if ((target).getHealth() <= 0) {
                continue;
            }

            if (closestTarget == null) {
                closestTarget = target;
                continue;
            }

            if (mc.player.getDistanceToEntity(target) < mc.player.getDistanceToEntity(closestTarget)) {
                closestTarget = target;
            }

        }

    }

    private boolean isInRange(BlockPos blockPos) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(
                getSphere(getPlayerPos(), smartRange, (int) smartRange, false, true, 0)
                        .stream().collect(Collectors.toList()));
        if (positions.contains(blockPos))
            return true;
        else
            return false;
    }

    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    private int findObiInHotbar() {

        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockObsidian) {
                slot = i;
                break;
            }

        }

        return slot;

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


    private static Block getBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock();
    }
    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(mc.world.getBlockState(pos), false);
    }

    private static class Offsets {

        private static final Vec3d[] TRAP = {
                new Vec3d(0, 0, -1),
                new Vec3d(1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 1, -1),
                new Vec3d(1, 1, 0),
                new Vec3d(0, 1, 1),
                new Vec3d(-1, 1, 0),
                new Vec3d(0, 2, -1),
                new Vec3d(1, 2, 0),
                new Vec3d(0, 2, 1),
                new Vec3d(-1, 2, 0),
                new Vec3d(0, 3, -1),
                new Vec3d(0, 3, 0)
        };

        private static final Vec3d[] BLOCKOVERHEADFACINGPOSX = {
                new Vec3d(1, 0, 0),
                new Vec3d(1, 1, 0),
                new Vec3d(1, 2, 0),
                new Vec3d(1, 3, 0),
                new Vec3d(0, 3, 0)
        };

        private static final Vec3d[] BLOCKOVERHEADFACINGPOSZ = {
                new Vec3d(0, 0, 1),
                new Vec3d(0, 1, 1),
                new Vec3d(0, 2, 1),
                new Vec3d(0, 3, 1),
                new Vec3d(0, 3, 0)
        };

        private static final Vec3d[] BLOCKOVERHEADFACINGNEGX = {
                new Vec3d(-1, 0, 0),
                new Vec3d(-1, 1, 0),
                new Vec3d(-1, 2, 0),
                new Vec3d(-1, 3, 0),
                new Vec3d(0, 3, 0)
        };

        private static final Vec3d[] BLOCKOVERHEADFACINGNEGZ = {
                new Vec3d(0, 0, -1),
                new Vec3d(0, 1, -1),
                new Vec3d(0, 2, -1),
                new Vec3d(0, 3, -1),
                new Vec3d(0, 3, 0)
        };

    }
}