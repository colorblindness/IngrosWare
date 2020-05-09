package best.reich.ingros.module.modules.combat;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.UpdateEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;


@ModuleManifest(label = "AutoOffhandGap", category = ModuleCategory.COMBAT, color = 0xffAEEA1E)
public class AutoOffhandGap extends ToggleableModule {
    @Clamp(minimum = "1", maximum = "22")
    @Setting("Health")
    public int health = 10;

    @Setting("ToggleTotem")
    public boolean toggletotem = true;

    @Setting("CrystalCheck")
    public boolean crystalCheck = true;

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.currentScreen instanceof GuiContainer)
            return;
        if (!shouldTotem()) {
            if (!(mc.player.getHeldItemOffhand() != ItemStack.EMPTY && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE)) {
                final int slot = getGappleSlot() < 9 ? getGappleSlot() + 36 : getGappleSlot();
                if (slot != -1) {
                    mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                }
            }
        } else if (!(mc.player.getHeldItemOffhand() != ItemStack.EMPTY && mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)) {
            final int slot = getTotemSlot() < 9 ? getTotemSlot() + 36 : getTotemSlot();
            if (slot != -1) {
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            }
        }
    }



    private boolean shouldTotem() {
        return (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= health || mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA || mc.player.fallDistance >= 3 || (crystalCheck && !isCrystalsAABBEmpty());
    }

    private boolean isEmpty(BlockPos pos){
        return mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).stream().filter(e -> e instanceof EntityEnderCrystal).count() == 0;
    }

    private boolean isCrystalsAABBEmpty(){
        return isEmpty(mc.player.getPosition().add(1, 0, 0)) && isEmpty(mc.player.getPosition().add(-1, 0, 0)) && isEmpty(mc.player.getPosition().add(0, 0, 1)) && isEmpty(mc.player.getPosition().add(0, 0, -1)) && isEmpty(mc.player.getPosition());
    }


    int getGappleSlot() {
        int crystalSlot = -1;
        for (int i = 45; i > 0; i--) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.GOLDEN_APPLE) {
                crystalSlot = i;
                break;
            }
        }
        return crystalSlot;
    }

    int getTotemSlot() {
        int totemSlot = -1;
        for (int i = 45; i > 0; i--) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                totemSlot = i;
                break;
            }
        }
        return totemSlot;
    }

    @Override
    public void onEnable() {
        if (toggletotem && IngrosWare.INSTANCE.moduleManager.getModule("AutoTotem").isEnabled())
            ((AutoTotem) IngrosWare.INSTANCE.moduleManager.getModule("AutoTotem")).toggle();
    }

    @Override
    public void onDisable() {
        if (toggletotem && !IngrosWare.INSTANCE.moduleManager.getModule("AutoTotem").isEnabled())
            ((AutoTotem) IngrosWare.INSTANCE.moduleManager.getModule("AutoTotem")).toggle();
    }
}