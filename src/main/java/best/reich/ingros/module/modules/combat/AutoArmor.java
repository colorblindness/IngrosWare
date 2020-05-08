package best.reich.ingros.module.modules.combat;

import best.reich.ingros.events.entity.UpdateEvent;
import net.b0at.api.event.types.EventType;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.util.math.TimerUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.b0at.api.event.Subscribe;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
@ModuleManifest(label = "AutoArmor", category = ModuleCategory.COMBAT, color = 0xD8BFFF)
public class AutoArmor extends ToggleableModule {
    private static final Item[] HELMETS = {Items.DIAMOND_HELMET, Items.IRON_HELMET, Items.GOLDEN_HELMET, Items.CHAINMAIL_HELMET, Items.LEATHER_HELMET};
    private static final Item[] CHESTPLATES = {Items.DIAMOND_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.LEATHER_CHESTPLATE};
    private static final Item[] LEGGINGS = {Items.DIAMOND_LEGGINGS, Items.IRON_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.LEATHER_LEGGINGS};
    private static final Item[] BOOTS = {Items.DIAMOND_BOOTS, Items.IRON_BOOTS, Items.GOLDEN_BOOTS, Items.CHAINMAIL_BOOTS, Items.LEATHER_BOOTS};
    private TimerUtil timer = new TimerUtil();

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;
        if(event.getType() == EventType.PRE) {
            int selectedSlotId = -1;
            if (timer.reach(100)) {
                if (mc.player.inventory.armorItemInSlot(2).getItem() == Items.AIR) {
                    for (Item item : CHESTPLATES) {
                        int slotId = getSlotID(item);
                        if (slotId != -1) {
                            selectedSlotId = slotId;
                        }
                    }
                }

                if (mc.player.inventory.armorItemInSlot(1).getItem() == Items.AIR) {
                    for (Item item : LEGGINGS) {
                        int slotId = getSlotID(item);
                        if (slotId != -1) {
                            selectedSlotId = slotId;
                        }
                    }
                }

                if (mc.player.inventory.armorItemInSlot(0).getItem() == Items.AIR) {
                    for (Item item : BOOTS) {
                        int slotId = getSlotID(item);
                        if (slotId != -1) {
                            selectedSlotId = slotId;
                        }
                    }
                }

                if (mc.player.inventory.armorItemInSlot(3).getItem() == Items.AIR) {
                    for (Item item : HELMETS) {
                        int slotId = getSlotID(item);
                        if (slotId != -1) {
                            selectedSlotId = slotId;
                        }
                    }
                }

                if (selectedSlotId != -1) {
                    if (selectedSlotId < 9)
                        selectedSlotId += 36;
                    mc.playerController.windowClick(0, selectedSlotId, 0, ClickType.QUICK_MOVE, mc.player);
                    timer.reset();
                }
            }
        }

    }

    public static int getSlotID(Item item) {
        for (int index = 0; index <= 36; index++) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(index);
            if (stack.getItem() == Items.AIR) continue;
            if (stack.getItem() == item) {
                return index;
            }
        }
        return -1;
    }
}
