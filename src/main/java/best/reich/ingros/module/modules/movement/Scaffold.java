package best.reich.ingros.module.modules.movement;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.other.SafeWalkEvent;
import best.reich.ingros.events.render.Render3DEvent;
import best.reich.ingros.util.game.InventoryUtil;
import best.reich.ingros.util.game.MotionUtil;
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Mode;
import me.xenforu.kelo.setting.annotation.Setting;
import me.xenforu.kelo.util.math.TimerUtil;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@ModuleManifest(label = "Scaffold", category = ModuleCategory.MOVEMENT, color = 0xffAfA033)
public class Scaffold extends ToggleableModule {

    private List<Block> invalid = Arrays.asList(Blocks.ANVIL, Blocks.AIR, Blocks.WATER, Blocks.FIRE, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.CHEST, Blocks.ENCHANTING_TABLE, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.GRAVEL, Blocks.LADDER, Blocks.VINE, Blocks.BEACON, Blocks.JUKEBOX
            , Blocks.ACACIA_DOOR, Blocks.BIRCH_DOOR, Blocks.DARK_OAK_DOOR, Blocks.IRON_DOOR, Blocks.JUNGLE_DOOR, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.IRON_TRAPDOOR, Blocks.TRAPDOOR
            , Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX);
    private TimerUtil timerMotion = new TimerUtil();
    private TimerUtil itemTimer = new TimerUtil();
    private BlockData blockData;

    @Setting("Expand")
    @Clamp(minimum = "0.1", maximum = "6")
    public double expand = 1;

    @Setting("ESPMode")
    @Mode({"Block", "Face"})
    public String espMode = "Block";

    @Setting("Switch")
    public boolean Switch = true;

    @Setting("Tower")
    public boolean tower = true;

    @Setting("Center")
    public boolean center = true;

    @Setting("KeepY")
    public boolean keepY = true;

    @Setting("Sprint")
    public boolean sprint = true;

    @Setting("ESP")
    public boolean esp = true;

    @Setting("KeepRotations")
    public boolean keepRots = true;

    @Setting("Replenish")
    public boolean replenishBlocks = true;

    @Setting("Down")
    public boolean down = true;

    @Setting("Swing")
    public boolean swing = false;

    private int lastY;
    private float lastYaw, lastPitch;
    private BlockPos pos;
    private boolean teleported;


    @Override
    public void onEnable() {
        if (mc.world != null) {
            this.timerMotion.reset();
            this.lastY = MathHelper.floor(mc.player.posY);
        }
    }

    @Subscribe
    public void onRender3D(final Render3DEvent event) {
        if (esp && blockData != null && blockData.position != null) {
            BlockPos place = blockData.position;
            double x1 = (espMode.equalsIgnoreCase("block") ? pos.getX() : place.getX()) - mc.getRenderManager().renderPosX;
            double x2 = (espMode.equalsIgnoreCase("block") ? pos.getX() : place.getX()) - mc.getRenderManager().renderPosX + 1;
            double y1 = (espMode.equalsIgnoreCase("block") ? pos.getY() : place.getY()) - mc.getRenderManager().renderPosY;
            double y2 = (espMode.equalsIgnoreCase("block") ? pos.getY() : place.getY()) - mc.getRenderManager().renderPosY + 1;
            double z1 = (espMode.equalsIgnoreCase("block") ? pos.getZ() : place.getZ()) - mc.getRenderManager().renderPosZ;
            double z2 = (espMode.equalsIgnoreCase("block") ? pos.getZ() : place.getZ()) - mc.getRenderManager().renderPosZ + 1;
            if (espMode.equalsIgnoreCase("face")) {
                EnumFacing face = blockData.face;
                y1 += face.getFrontOffsetY();
                if (face.getFrontOffsetX() < 0) {
                    x2 += face.getFrontOffsetX();
                } else {
                    x1 += face.getFrontOffsetX();
                }
                if (face.getFrontOffsetZ() < 0) {
                    z2 += face.getFrontOffsetZ();
                } else {
                    z1 += face.getFrontOffsetZ();
                }
            }
            Color clr = new Color(117, 255, 253, 29);
            RenderUtil.drawESP(new AxisAlignedBB(x1, y1, z1, x2, y2, z2), clr.getRed(), clr.getGreen(), clr.getBlue(), clr.getAlpha());
        }
    }


    @Subscribe
    public void onUpdate(final UpdateEvent event) {
        int downDistance;
        if (!IngrosWare.INSTANCE.moduleManager.getModule("Sprint").isEnabled()) {
            if ((down && mc.gameSettings.keyBindSneak.isKeyDown()) || !sprint)
                mc.player.setSprinting(false);
        }
        if (down && mc.gameSettings.keyBindSneak.isKeyDown()) {
            downDistance = 2;
        } else {
            downDistance = 1;
        }
        if (replenishBlocks && !(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) && getBlockCountHotbar() <= 0 && this.itemTimer.reach(100L)) {
            for (int i = 9; i < 45; ++i) {
                if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                    final ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                    if (is.getItem() instanceof ItemBlock && !invalid.contains(Block.getBlockFromItem(is.getItem()))) {
                        if (i < 36) {
                            InventoryUtil.swap(InventoryUtil.getItemSlot(mc.player.inventoryContainer, is.getItem()), 44);
                            // mc.playerController.windowClick(mc.player.inventoryContainer.windowId, InventoryUtil.getItemSlot(mc.player.inventoryContainer, is.getItem()), 44, ClickType.SWAP, mc.player);
                            //mc.playerController.updateController();

                        }
                    }
                }
            }
        }
        if (this.keepY) {
            if ((!MotionUtil.isMoving(mc.player) && mc.gameSettings.keyBindJump.isKeyDown()) || mc.player.isCollidedVertically || mc.player.onGround) {
                this.lastY = MathHelper.floor(mc.player.posY);
            }
        }
        else {
            this.lastY = MathHelper.floor(mc.player.posY);
        }
        if (event.getType().equals(EventType.PRE)) {
            this.blockData = null;
            double x = mc.player.posX;
            double z = mc.player.posZ;
            double y = keepY ? this.lastY : mc.player.posY;
            double forward = mc.player.movementInput.moveForward;
            double strafe = mc.player.movementInput.moveStrafe;
            float yaw = mc.player.rotationYaw;
            if (!mc.player.isCollidedHorizontally){
                double[] coords = getExpandCoords(x,z,forward,strafe,yaw);
                x = coords[0];
                z = coords[1];
            }
            if (canPlace(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - downDistance, mc.player.posZ)).getBlock())) {
                x = mc.player.posX;
                z = mc.player.posZ;
            }
            BlockPos blockBelow = new BlockPos(x, y-downDistance, z);
            pos = blockBelow;
            if (mc.world.getBlockState(blockBelow).getBlock() == Blocks.AIR) {
                this.blockData = this.getBlockData2(blockBelow);
                if (this.blockData != null) {
                    float yaw1 = this.aimAtLocation(this.blockData.position.getX(), this.blockData.position.getY(), this.blockData.position.getZ(), this.blockData.face)[0];
                    float pitch = this.aimAtLocation(this.blockData.position.getX(), this.blockData.position.getY(), this.blockData.position.getZ(), this.blockData.face)[1];
                    if (keepRots) {
                        lastYaw = yaw1;
                        lastPitch = pitch;
                    } else {
                        event.setYaw(yaw1);
                        event.setPitch(pitch);
                    }
                }
            }
            if (keepRots) {
                event.setYaw(lastYaw);
                event.setPitch(lastPitch);
            }
        }
        else if (this.blockData != null) {
            if (this.getBlockCountHotbar() <= 0 || (!this.Switch && mc.player.getHeldItemMainhand().getItem() != null && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock))) {
                return;
            }
            final int heldItem = mc.player.inventory.currentItem;
            if (this.Switch) {
                for (int j = 0; j < 9; ++j) {
                    if (mc.player.inventory.getStackInSlot(j) != null && mc.player.inventory.getStackInSlot(j).getCount() != 0 && mc.player.inventory.getStackInSlot(j).getItem() instanceof ItemBlock && !this.invalid.contains(((ItemBlock)mc.player.inventory.getStackInSlot(j).getItem()).getBlock())) {
                        mc.player.inventory.currentItem = j;
                        break;
                    }
                }
            }
            if (this.tower) {
                if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f && this.tower && !mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    if (!teleported && center) {
                        teleported = true;
                        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
                        mc.player.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    }
                    if (center && !teleported)
                        return;
                    mc.player.motionY = 0.42f;
                    mc.player.motionZ = 0;
                    mc.player.motionX = 0;
                    if (this.timerMotion.sleep(1500L)) {
                        mc.player.motionY = -0.28;
                    }
                }
                else {
                    this.timerMotion.reset();
                    if (teleported && center)
                        teleported = false;
                }
            }
            if (mc.playerController.processRightClickBlock(mc.player, mc.world, this.blockData.position, this.blockData.face, new Vec3d(this.blockData.position.getX() + Math.random(), this.blockData.position.getY() + Math.random(), this.blockData.position.getZ() + Math.random()), EnumHand.MAIN_HAND) != EnumActionResult.FAIL) {
                if (swing) mc.player.swingArm(EnumHand.MAIN_HAND);
                else  mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            }
            mc.player.inventory.currentItem = heldItem;
        }
    }

    private int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; ++i) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                final Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock) {
                    if (!this.invalid.contains(((ItemBlock)item).getBlock())) {
                        blockCount += is.getCount();
                    }
                }
            }
        }
        return blockCount;
    }

    public double[] getExpandCoords(double x, double z, double forward, double strafe, float YAW){
        BlockPos underPos = new BlockPos(x, mc.player.posY - (mc.gameSettings.keyBindSneak.isKeyDown() && down ? 2 : 1), z);
        Block underBlock = mc.world.getBlockState(underPos).getBlock();
        double xCalc = -999, zCalc = -999;
        double dist = 0;
        double expandDist = expand * 2;
        while(!canPlace(underBlock)){
            xCalc = x;
            zCalc = z;
            dist ++;
            if(dist > expandDist){
                dist = expandDist;
            }
            xCalc += (forward * 0.45 * Math.cos(Math.toRadians(YAW + 90.0f)) + strafe * 0.45 * Math.sin(Math.toRadians(YAW + 90.0f))) * dist;
            zCalc += (forward * 0.45 * Math.sin(Math.toRadians(YAW + 90.0f)) - strafe * 0.45 * Math.cos(Math.toRadians(YAW + 90.0f))) * dist;
            if(dist == expandDist){
                break;
            }
            underPos = new BlockPos(xCalc, mc.player.posY - (mc.gameSettings.keyBindSneak.isKeyDown() && down ? 2 : 1), zCalc);
            underBlock = mc.world.getBlockState(underPos).getBlock();
        }
        return new double[]{xCalc,zCalc};
    }

    public boolean canPlace(Block block) {
        return (block instanceof BlockAir || block instanceof BlockLiquid) && mc.world != null && mc.player != null && pos != null &&mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty();
    }

    private int getBlockCountHotbar() {
        int blockCount = 0;
        for (int i = 36; i < 45; ++i) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                final Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock) {
                    if (!this.invalid.contains(((ItemBlock)item).getBlock())) {
                        blockCount += is.getCount();
                    }
                }
            }
        }
        return blockCount;
    }

    private BlockData getBlockData2(final BlockPos pos) {
        if (!this.invalid.contains(mc.world.getBlockState(pos.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos.add(0, 1, 0), EnumFacing.DOWN);
        }
        final BlockPos pos2 = pos.add(-1, 0, 0);
        if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos2.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos2.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos2.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos3 = pos.add(1, 0, 0);
        if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos3.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos3.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos3.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos4 = pos.add(0, 0, 1);
        if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos4.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos4.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos4.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos5 = pos.add(0, 0, -1);
        if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos5.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos5.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos5.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos6 = pos.add(-2, 0, 0);
        if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos2.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos2.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos2.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos7 = pos.add(2, 0, 0);
        if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos3.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos3.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos3.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos8 = pos.add(0, 0, 2);
        if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos4.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos4.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos4.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos9 = pos.add(0, 0, -2);
        if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos5.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos5.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos5.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos10 = pos.add(0, -1, 0);
        if (!this.invalid.contains(mc.world.getBlockState(pos10.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos10.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos10.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos10.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos10.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos10.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos10.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos10.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos10.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos10.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos10.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos10.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos11 = pos10.add(1, 0, 0);
        if (!this.invalid.contains(mc.world.getBlockState(pos11.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos11.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos11.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos11.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos11.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos11.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos11.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos11.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos11.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos11.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos11.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos11.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos12 = pos10.add(-1, 0, 0);
        if (!this.invalid.contains(mc.world.getBlockState(pos12.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos12.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos12.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos12.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos12.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos12.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos12.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos12.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos12.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos12.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos12.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos12.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos13 = pos10.add(0, 0, 1);
        if (!this.invalid.contains(mc.world.getBlockState(pos13.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos13.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos13.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos13.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos13.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos13.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos13.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos13.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos13.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos13.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos13.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos13.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos14 = pos10.add(0, 0, -1);
        if (!this.invalid.contains(mc.world.getBlockState(pos14.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos14.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos14.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos14.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos14.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos14.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos14.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos14.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos14.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos14.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(mc.world.getBlockState(pos14.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos14.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }// aids

    private float[] aimAtLocation(final double x, final double y, final double z, final EnumFacing facing) {
        final EntitySnowball temp = new EntitySnowball(mc.world);
        temp.posX = x + 0.5;
        temp.posY = y - 2.7035252353;
        temp.posZ = z + 0.5;
        return this.aimAtLocation(temp.posX, temp.posY, temp.posZ);
    }

    private float[] aimAtLocation(final double positionX, final double positionY, final double positionZ) {
        final double x = positionX - mc.player.posX;
        final double y = positionY - mc.player.posY;
        final double z = positionZ - mc.player.posZ;
        final double distance = MathHelper.sqrt(x * x + z * z);
        return new float[] { (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f, (float)(-(Math.atan2(y, distance) * 180.0 / 3.141592653589793))};
    }

    private class BlockData {
        public BlockPos position;
        public EnumFacing face;

        public BlockData(final BlockPos position, final EnumFacing face) {
            this.position = position;
            this.face = face;
        }
    }

}
