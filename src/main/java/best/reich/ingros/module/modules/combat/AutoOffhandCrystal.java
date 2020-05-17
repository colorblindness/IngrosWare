package best.reich.ingros.module.modules.combat;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.util.game.GameUtil;
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


@ModuleManifest(label = "AutoOffhandCrystal", category = ModuleCategory.COMBAT, color = 0xffFFEA1E)
public class AutoOffhandCrystal extends ToggleableModule {
    @Clamp(minimum = "1", maximum = "22")
    @Setting("Health")
    public int health = 20;

    @Setting("ToggleTotem")
    public boolean toggletotem = true;

    @Setting("CrystalCheck")
    public boolean crystalCheck = true;

    @Setting("HoleCheck")
    public boolean holeCheck = true;

    @Clamp(minimum = "1", maximum = "22")
    @Setting("HoleHealth")
    public int holeHealth = 8;

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.currentScreen instanceof GuiContainer)
            return;
        if (!shouldTotem()) {
            if (!(mc.player.getHeldItemOffhand() != ItemStack.EMPTY && mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)) {
                final int slot = getCrystalSlot() < 9 ? getCrystalSlot() + 36 : getCrystalSlot();
                if (getCrystalSlot() != -1) {
                    mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                }
            }
        } else if (!(mc.player.getHeldItemOffhand() != ItemStack.EMPTY && mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)) {
            final int slot = getTotemSlot() < 9 ? getTotemSlot() + 36 : getTotemSlot();
            if (getTotemSlot() != -1) {
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            }
        }
    }

    private boolean nearPlayers() {
        return mc.world.playerEntities.stream().anyMatch(e -> e != mc.player && e.getEntityId() != -1488 && !IngrosWare.INSTANCE.friendManager.isFriend(e.getName()) && mc.player.getDistanceToEntity(e) <= 10);
    }

    private boolean shouldTotem() {
        if (holeCheck && GameUtil.isInHole(mc.player)) {
            return (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= holeHealth || mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA || !nearPlayers() || mc.player.fallDistance >= 3 || (crystalCheck && !isCrystalsAABBEmpty());
        }
        return (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= health || mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA || !nearPlayers() || mc.player.fallDistance >= 3 || (crystalCheck && !isCrystalsAABBEmpty());
    }

    private boolean isEmpty(BlockPos pos){
        return mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).stream().filter(e -> e instanceof EntityEnderCrystal).count() == 0;
    }

    private boolean isCrystalsAABBEmpty(){
        return isEmpty(mc.player.getPosition().add(1, 0, 0)) && isEmpty(mc.player.getPosition().add(-1, 0, 0)) && isEmpty(mc.player.getPosition().add(0, 0, 1)) && isEmpty(mc.player.getPosition().add(0, 0, -1)) && isEmpty(mc.player.getPosition());
    }

    int getCrystalSlot() {
        int crystalSlot = -1;
        for (int i = 45; i > 0; i--) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
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