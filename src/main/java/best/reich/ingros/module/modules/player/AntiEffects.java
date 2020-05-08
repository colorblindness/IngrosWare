package best.reich.ingros.module.modules.player;

import best.reich.ingros.events.network.PacketEvent;
import net.b0at.api.event.types.EventType;
import com.google.common.collect.Sets;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.b0at.api.event.Subscribe;

import java.util.Set;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
@ModuleManifest(label = "AntiEffects", category = ModuleCategory.PLAYER, color = 0xFF05B21C,hidden = true)
public class AntiEffects extends ToggleableModule {
    private final Set<Integer> DEBUFFS = Sets.newHashSet(25, 15, 9);

    @Subscribe
    public void onReadPacket(PacketEvent event) {
        if (mc.player == null) return;
        if(event.getType() == EventType.POST) {
            if (mc.world != null && event.getPacket() instanceof SPacketEntityEffect) {
                SPacketEntityEffect effect = (SPacketEntityEffect) event.getPacket();
                if (effect.getEntityId() == mc.player.getEntityId() && DEBUFFS.contains((int)effect.getEffectId())) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
