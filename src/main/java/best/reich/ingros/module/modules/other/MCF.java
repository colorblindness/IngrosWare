package best.reich.ingros.module.modules.other;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.UpdateEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

@ModuleManifest(label = "MCF", category = ModuleCategory.OTHER, color = 0xff00ff00, hidden = true)
public class MCF extends ToggleableModule {

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (Mouse.isButtonDown(2)) {
            final Entity entity = mc.objectMouseOver.entityHit;
            if (entity instanceof EntityPlayer) {
                if (!IngrosWare.INSTANCE.friendManager.isFriend(entity.getName())) {
                    IngrosWare.INSTANCE.friendManager.addFriend(entity.getName());
                } else {
                    IngrosWare.INSTANCE.friendManager.removeFriend(entity.getName());
                }
            }
        }
    }

}
