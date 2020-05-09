package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.other.TraceEntityEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

@ModuleManifest(label = "NoEntityTrace", category = ModuleCategory.OTHER, color = 0xff3F63fe,hidden = true)
public class NoEntityTrace extends ToggleableModule {

    @Subscribe
    public void onEntityTrace(TraceEntityEvent event) {
        event.setCancelled(mc.player.getHeldItemMainhand() != ItemStack.EMPTY && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe);
    }
}
