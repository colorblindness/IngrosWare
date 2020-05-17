package best.reich.ingros.module.modules.movement;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.other.BoundingBoxEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;


@ModuleManifest(label = "Jesus", category = ModuleCategory.MOVEMENT, color = 0xff2020ff)
public class Jesus extends ToggleableModule {

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || event.getType() == EventType.POST || (mc.player.isBurning() && isOnWater()) || mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown() || mc.player.fallDistance > 3)
            return;
        if (event.getType().equals(EventType.PRE)) {
            if (isInLiquid()) mc.player.motionY = 0.1;
            if (isOnLiquid() && !isInLiquid()) {
                event.setY(event.getY() + (mc.player.ticksExisted % 2 == 0 ? 0.000000000002000111 : 0));
                event.setOnGround(mc.player.ticksExisted % 2 != 0);
            }
        }
    }

    @Subscribe
    public void onAddCollisionBoxToList(BoundingBoxEvent event) {
        if (mc.player == null) return;
        if (((event.getBlock() instanceof BlockLiquid)) && event.getEntity() == mc.player
                && !isInLiquid() && (mc.player.fallDistance < 3.0F) && (!mc.player.isSneaking())) {
            event.setAabb(Block.FULL_BLOCK_AABB);
        }
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
