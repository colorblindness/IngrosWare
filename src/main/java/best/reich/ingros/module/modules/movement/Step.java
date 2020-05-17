package best.reich.ingros.module.modules.movement;

import best.reich.ingros.events.entity.UpdateEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

@ModuleManifest(label = "Step", category = ModuleCategory.MOVEMENT, color = 0xffffff33)
public class Step extends ToggleableModule {
    private final double[] oneblockPositions = {0.42D, 0.75D};
    private final double[] twoblockPositions = {0.4D, 0.75D, 0.5D, 0.41D, 0.83D, 1.16D, 1.41D, 1.57D, 1.58D, 1.42D};
    private int packets;

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == EventType.POST) {
            if (mc.player.isCollidedHorizontally && mc.player.onGround && (isStepableOne(mc.player) || isStepableTwo(mc.player))) {
                this.packets++;
            }
            if (mc.player.onGround && isStepableOne(mc.player) && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && mc.player.fallDistance == 0 && !mc.gameSettings.keyBindJump.isKeyDown() && mc.player.isCollidedHorizontally && !mc.player.isOnLadder() && this.packets > 0) {
                for (double position : this.oneblockPositions) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + position, mc.player.posZ, true));
                }
                mc.player.setPosition(mc.player.posX, mc.player.posY + this.oneblockPositions[this.oneblockPositions.length - 1], mc.player.posZ);
                this.packets = 0;
            }
            if (mc.player.onGround && isStepableTwo(mc.player) && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && mc.player.fallDistance == 0 && !mc.gameSettings.keyBindJump.isKeyDown() && mc.player.isCollidedHorizontally && !mc.player.isOnLadder() && this.packets > 0) {
                for (double position : this.twoblockPositions) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + position, mc.player.posZ, true));
                }
                mc.player.setPosition(mc.player.posX, mc.player.posY + this.twoblockPositions[this.twoblockPositions.length - 1], mc.player.posZ);
                this.packets = 0;
            }
        }
    }
    private boolean isStepableTwo(EntityPlayerSP player) {
        if (isOnLiquid() || isInLiquid()) return false;
        ArrayList<BlockPos> collisionBlocks = new ArrayList<>();
        BlockPos pos1 = new BlockPos(player.getEntityBoundingBox().minX - 0.001D, player.getEntityBoundingBox().minY - 0.001D, player.getEntityBoundingBox().minZ - 0.001D);
        BlockPos pos2 = new BlockPos(player.getEntityBoundingBox().maxX + 0.001D, player.getEntityBoundingBox().maxY + 0.001D, player.getEntityBoundingBox().maxZ + 0.001D);

        if (player.world.isAreaLoaded(pos1, pos2)) for (int x = pos1.getX(); x <= pos2.getX(); x++)
            for (int y = pos1.getY(); y <= pos2.getY() + 2; y++)
                for (int z = pos1.getZ(); z <= pos2.getZ(); z++)
                    if (y > player.posY && y <= player.posY + 1) collisionBlocks.add(new BlockPos(x, y, z));

        for (BlockPos collisionBlock : collisionBlocks)
            if (!(mc.world.getBlockState(collisionBlock.add(0, 3, 0)).getBlock() instanceof BlockAir || mc.world.getBlockState(collisionBlock.add(0, 3, 0)).getBlock() instanceof BlockFlower || mc.world.getBlockState(collisionBlock.add(0, 3, 0)).getBlock() instanceof BlockGrass || mc.world.getBlockState(collisionBlock.add(0, 3, 0)).getBlock() instanceof BlockTallGrass)||!(mc.world.getBlockState(collisionBlock.add(0, 2, 0)).getBlock() instanceof BlockAir || mc.world.getBlockState(collisionBlock.add(0, 2, 0)).isFullBlock()|| mc.world.getBlockState(collisionBlock.add(0, 2, 0)).getBlock() instanceof BlockFlower || mc.world.getBlockState(collisionBlock.add(0, 2, 0)).getBlock() instanceof BlockGrass || mc.world.getBlockState(collisionBlock.add(0, 2, 0)).getBlock() instanceof BlockTallGrass) || !(mc.world.getBlockState(collisionBlock.add(0, 1, 0)).getBlock() instanceof BlockAir || mc.world.getBlockState(collisionBlock.add(0, 1, 0)).getBlock() instanceof BlockFlower || mc.world.getBlockState(collisionBlock.add(0, 1, 0)).getBlock() instanceof BlockGrass || mc.world.getBlockState(collisionBlock.add(0, 1, 0)).getBlock() instanceof BlockTallGrass) || !player.isCollidedHorizontally || !player.onGround || !(player.movementInput.forwardKeyDown || player.movementInput.backKeyDown || player.movementInput.leftKeyDown || player.movementInput.rightKeyDown))
                return false;

        return true;
    }
    private boolean isStepableOne(EntityPlayerSP player) {
        if (isOnLiquid() || isInLiquid()) return false;
        ArrayList<BlockPos> collisionBlocks = new ArrayList<>();
        BlockPos pos1 = new BlockPos(player.getEntityBoundingBox().minX - 0.001D, player.getEntityBoundingBox().minY - 0.001D, player.getEntityBoundingBox().minZ - 0.001D);
        BlockPos pos2 = new BlockPos(player.getEntityBoundingBox().maxX + 0.001D, player.getEntityBoundingBox().maxY + 0.001D, player.getEntityBoundingBox().maxZ + 0.001D);

        if (player.world.isAreaLoaded(pos1, pos2)) for (int x = pos1.getX(); x <= pos2.getX(); x++)
            for (int y = pos1.getY(); y <= pos2.getY() + 1; y++)
                for (int z = pos1.getZ(); z <= pos2.getZ(); z++)
                    if (y > player.posY - 1.0D && y <= player.posY) collisionBlocks.add(new BlockPos(x, y, z));

        for (BlockPos collisionBlock : collisionBlocks)
            if (!(mc.world.getBlockState(collisionBlock.add(0, 2, 0)).getBlock() instanceof BlockAir || mc.world.getBlockState(collisionBlock.add(0, 1, 0)).isFullBlock()|| mc.world.getBlockState(collisionBlock.add(0, 2, 0)).getBlock() instanceof BlockFlower || mc.world.getBlockState(collisionBlock.add(0, 2, 0)).getBlock() instanceof BlockGrass || mc.world.getBlockState(collisionBlock.add(0, 2, 0)).getBlock() instanceof BlockTallGrass) || !(mc.world.getBlockState(collisionBlock.add(0, 1, 0)).getBlock() instanceof BlockAir || mc.world.getBlockState(collisionBlock.add(0, 1, 0)).getBlock() instanceof BlockFlower || mc.world.getBlockState(collisionBlock.add(0, 1, 0)).getBlock() instanceof BlockGrass || mc.world.getBlockState(collisionBlock.add(0, 1, 0)).getBlock() instanceof BlockTallGrass) || !player.isCollidedHorizontally || !player.onGround || !(player.movementInput.forwardKeyDown || player.movementInput.backKeyDown || player.movementInput.leftKeyDown || player.movementInput.rightKeyDown))
                return false;

        return true;
    }

    private boolean isOnLiquid() {
        final double y = mc.player.posY - 0.03;
        for (int x = MathHelper.floor(mc.player.posX); x < MathHelper.ceil(mc.player.posX); ++x) {
            for (int z = MathHelper.floor(mc.player.posZ); z < MathHelper.ceil(mc.player.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInLiquid() {
        final double y = mc.player.posY + 0.01;
        for (int x = MathHelper.floor(mc.player.posX); x < MathHelper.ceil(mc.player.posX); ++x) {
            for (int z = MathHelper.floor(mc.player.posZ); z < MathHelper.ceil(mc.player.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, (int) y, z);
                if (mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }
}