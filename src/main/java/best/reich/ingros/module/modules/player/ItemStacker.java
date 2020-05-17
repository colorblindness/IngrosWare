package best.reich.ingros.module.modules.player;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.util.thealtening.utils.Pair;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;


@ModuleManifest(label = "ItemStacker", category = ModuleCategory.PLAYER, color = 0xA5AEAD)
public class ItemStacker extends ToggleableModule {
    @Setting("TickDelay")
    @Clamp(minimum = "1", maximum = "10")
    public int tickDelay = 2;
    @Setting("Threshold")
    @Clamp(minimum = "1", maximum = "63")
    public int threshold = 32;

    private int delayStep = 0;

    private Map<Integer, ItemStack> getInventory() {
        return getInventorySlots(9, 35);
    }

    private Map<Integer, ItemStack> getHotbar() {
        return getInventorySlots(36, 44);
    }

    private Map<Integer, ItemStack> getInventorySlots(int current, int last) {
        final Map<Integer, ItemStack> fullInventorySlots = new HashMap<>();
        while (current <= last) {
            fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
            current++;
        }
        return fullInventorySlots;
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) {
            return;
        }
        if (mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (delayStep < tickDelay) {
            delayStep++;
            return;
        } else {
            delayStep = 0;
        }
        final Pair<Integer, Integer> slots = findReplenishableHotbarSlot();
        if (slots == null) {
            return;
        }
        final int inventorySlot = slots.getKey();
        final int hotbarSlot = slots.getValue();
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, hotbarSlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
    }

    private Pair<Integer, Integer> findReplenishableHotbarSlot() {
        Pair<Integer, Integer> returnPair = null;
        for (Map.Entry<Integer, ItemStack> hotbarSlot : getHotbar().entrySet()) {

            ItemStack stack = hotbarSlot.getValue();

            if (stack.isEmpty() || stack.getItem() == Items.AIR) {
                continue;
            }

            if (!stack.isStackable()) {
                continue;
            }

            if (stack.getCount() >= stack.getMaxStackSize()) {
                continue;
            }

            if (stack.getCount() > threshold) {
                continue;
            }

            int inventorySlot = findCompatibleInventorySlot(stack);

            if (inventorySlot == -1) {
                continue;
            }

            returnPair = new Pair<>(inventorySlot, hotbarSlot.getKey());

        }
        return returnPair;

    }

    private int findCompatibleInventorySlot(ItemStack hotbarStack) {

        int inventorySlot = -1;
        int smallestStackSize = 999;

        for (Map.Entry<Integer, ItemStack> entry : getInventory().entrySet()) {

            ItemStack inventoryStack = entry.getValue();

            if (inventoryStack.isEmpty() || inventoryStack.getItem() == Items.AIR) {
                continue;
            }

            if (!isCompatibleStacks(hotbarStack, inventoryStack)) {
                continue;
            }

            int currentStackSize = mc.player.inventoryContainer.getInventory().get(entry.getKey()).getCount();

            if (smallestStackSize > currentStackSize) {
                smallestStackSize = currentStackSize;
                inventorySlot = entry.getKey();
            }
        }
        return inventorySlot;
    }

    private boolean isCompatibleStacks(ItemStack stack1, ItemStack stack2) {
        if (!stack1.getItem().equals(stack2.getItem())) {
            return false;
        }

        if ((stack1.getItem() instanceof ItemBlock) && (stack2.getItem() instanceof ItemBlock)) {
            final Block block1 = ((ItemBlock) stack1.getItem()).getBlock();
            final Block block2 = ((ItemBlock) stack2.getItem()).getBlock();
            if (!block1.getMaterial(block1.getBlockState().getBaseState()).equals(block2.getMaterial(block2.getBlockState().getBaseState()))) {
                return false;
            }
        }
        if (!stack1.getDisplayName().equals(stack2.getDisplayName())) {
            return false;
        }
        return stack1.getItemDamage() == stack2.getItemDamage();
    }
}
