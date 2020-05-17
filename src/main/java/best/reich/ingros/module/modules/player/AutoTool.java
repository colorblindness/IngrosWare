package best.reich.ingros.module.modules.player;

import best.reich.ingros.events.network.PacketEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

@ModuleManifest(label = "AutoTool", category = ModuleCategory.PLAYER, color = 0xfffffAEF)
public class AutoTool extends ToggleableModule {
    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getType() == EventType.PRE) {
            if (event.getPacket() instanceof CPacketPlayerDigging) {
                final CPacketPlayerDigging packetPlayerDigging = (CPacketPlayerDigging) event.getPacket();
                if ((packetPlayerDigging.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                    if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && !mc.player.capabilities.isCreativeMode) {
                        mc.player.inventory.currentItem = getBestTool(packetPlayerDigging.getPosition());
                        mc.playerController.updateController();
                    }
                }
            }
        }
    }

    private int getBestTool(BlockPos pos) {
        final Block block = mc.world.getBlockState(pos).getBlock();
        int slot = 0;
        float dmg = 0.1F;
        for (int index = 36; index < 45; index++) {
            final ItemStack itemStack = mc.player.inventoryContainer
                    .getSlot(index).getStack();
            if (itemStack != ItemStack.EMPTY
                    && block != Blocks.AIR
                    && itemStack.getItem().getStrVsBlock(itemStack, mc.world.getBlockState(pos)) > dmg) {
                slot = index - 36;
                dmg = itemStack.getItem().getStrVsBlock(itemStack, mc.world.getBlockState(pos));
            }
        }
        if (dmg > 0.1F) {
            return slot;
        }
        return mc.player.inventory.currentItem;
    }
}
