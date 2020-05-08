package best.reich.ingros.module.modules;

import best.reich.ingros.events.other.EntityChunkEvent;
import best.reich.ingros.util.logging.Logger;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.minecraft.entity.player.EntityPlayer;
import net.b0at.api.event.Subscribe;


@ModuleManifest(label = "Notifications", category = ModuleCategory.OTHER, color = 0xff007777,hidden = true)
public class Notifications extends ToggleableModule {

    @Subscribe
    public void onEntityEnterChunk(EntityChunkEvent event) {
        if (mc.world == null || mc.player == null) return;
        switch (event.getType()) {
            case PRE:
                if (event.getEntity() instanceof EntityPlayer && !event.getEntity().getName().equals(mc.player.getName())) {
                    Logger.printMessage(event.getEntity().getName() + " has entered your view distance!");
                }
                break;
            case POST:
                if (event.getEntity() instanceof EntityPlayer && !event.getEntity().getName().equals(mc.player.getName())) {
                    Logger.printMessage(event.getEntity().getName() + " has left your view distance!");
                }
                break;
        }
    }

}
