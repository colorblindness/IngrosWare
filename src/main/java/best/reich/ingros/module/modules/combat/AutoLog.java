package best.reich.ingros.module.modules.combat;

import best.reich.ingros.events.entity.UpdateEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

@ModuleManifest(label = "AutoLog", category = ModuleCategory.COMBAT, color = 0xff400070)
public class AutoLog extends ToggleableModule {

    @Subscribe
    public void onUpdatePre(UpdateEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.PRE) {
            if (mc.isSingleplayer()) return;
            if (!hasTotems() && mc.player.getHealth() < 6) {
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketHeldItemChange(420));
                toggle();
            }
        }
    }

    private boolean hasTotems() {
        if (mc.player.getHeldItemOffhand() != ItemStack.EMPTY && mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
            return true;
        for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
            if (mc.player.inventory.mainInventory.get(i) != ItemStack.EMPTY && mc.player.inventory.mainInventory.get(i).getItem() == Items.TOTEM_OF_UNDYING)
                return true;
        }
        return false;
    }
}
