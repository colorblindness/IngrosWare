package best.reich.ingros.module.modules.player;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.mixin.accessors.ICPacketPlayer;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;

@ModuleManifest(label = "AntiHunger", category = ModuleCategory.PLAYER, color = 0xffffff00)
public class AntiHunger extends ToggleableModule {
    private boolean wasOnGround;

    @Subscribe
    public void sendPacket(PacketEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.PRE) {
            if (IngrosWare.INSTANCE.moduleManager.getToggleByName("flight").isEnabled() || IngrosWare.INSTANCE.moduleManager.getToggleByName("speed").isEnabled())
                return;
            if (event.getPacket() instanceof CPacketPlayer) {
                final ICPacketPlayer packet = (ICPacketPlayer) event.getPacket();
                if (mc.player.isSprinting() && !mc.player.onGround && mc.gameSettings.keyBindJump.isKeyDown())
                    mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                if (!wasOnGround && mc.player.onGround) {
                    packet.setOnGround(true);
                    return;
                }
                if (mc.playerController.getIsHittingBlock()) {
                    return;
                }
                packet.setOnGround(false);
            }
        }
    }

    @Subscribe
    public void onUpdatePost(UpdateEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.POST) {
            this.wasOnGround = mc.player.onGround;
        }
    }

}
