package best.reich.ingros.module.modules.movement;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.events.other.BoundingBoxEvent;
import best.reich.ingros.mixin.accessors.ICPacketPlayer;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;


@ModuleManifest(label = "Jesus", category = ModuleCategory.MOVEMENT, color = 0xff2020ff)
public class Jesus extends ToggleableModule {
    private static final AxisAlignedBB WATER_WALK_AA = new AxisAlignedBB(0.D, 0.D, 0.D, 1.D, 0.99D, 1.D);

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == EventType.POST || (mc.player.isBurning() && isOnWater())) return;
        if (isInLiquid() && !mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown() && mc.player.fallDistance < 3)
            mc.player.motionY = 0.1;
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getType() == EventType.PRE) {
            if (mc.player != null) {
                if (!(event.getPacket() instanceof CPacketPlayer) || isInLiquid() || !isOnLiquid() || mc.player.isSneaking() || mc.player.fallDistance > 3 || (mc.player.isBurning() && isOnWater()))
                    return;
                ICPacketPlayer packet = (ICPacketPlayer) event.getPacket();
                if (mc.player.isSprinting() && mc.player.isInLava() && isOnLiquid())
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                packet.setY(packet.getY() + (mc.player.ticksExisted % 2 == 0 ? 0.000000000002000111 : 0));
                packet.setOnGround(mc.player.ticksExisted % 2 != 0);
            }
        }
    }

    @Subscribe
    public void onAddCollisionBoxToList(BoundingBoxEvent event) {
        if (mc.player != null) {
            if (mc.world == null || mc.player.fallDistance > 3 || (mc.player.isBurning() && isOnWater()))
                return;
            Block block = mc.world.getBlockState(event.getPos()).getBlock();
            if (!isOnLiquid() || !(block instanceof BlockLiquid) || isInLiquid() || mc.player.isSneaking())
                return;
            event.getCollidingBoxes().add(new AxisAlignedBB(0, 0, 0, 1, 1, 1).contract(0, 0.000000000002000111, 0).offset(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()));
        }
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

    private boolean isOnWater() {
        final double y = mc.player.posY - 0.03;
        for (int x = MathHelper.floor(mc.player.posX); x < MathHelper.ceil(mc.player.posX); ++x) {
            for (int z = MathHelper.floor(mc.player.posZ); z < MathHelper.ceil(mc.player.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid && mc.world.getBlockState(pos).getBlock() == Blocks.WATER) {
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
