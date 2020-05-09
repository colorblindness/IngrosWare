package best.reich.ingros.module.modules.movement;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.UpdateInputEvent;
import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.mixin.accessors.ICPacketPlayer;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.client.CPacketPlayer;

@ModuleManifest(label = "NoSlowDown", category = ModuleCategory.MOVEMENT, color = 0x666666)
public class NoSlowDown extends ToggleableModule {
    @Setting("NCP")
    public boolean ncp = true;

    @Subscribe
    public void onInputUpdate(UpdateInputEvent event) {
        if (mc.player.isHandActive() && !mc.player.isRiding()) {
            mc.player.movementInput.moveStrafe *= 5;
            mc.player.movementInput.moveForward *= 5;
        }
    }

    @Subscribe
    public void onPre(PacketEvent event) {
        if (event.getType() == EventType.PRE && event.getPacket() instanceof CPacketPlayer && ncp && mc.player.isActiveItemStackBlocking() && mc.player.onGround && !IngrosWare.INSTANCE.moduleManager.getModule("Speed").isEnabled() && (mc.player.moveStrafing != 0 || mc.player.moveForward != 0) && !mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY = 0.02;
            ((ICPacketPlayer)event.getPacket()).setY(((ICPacketPlayer)event.getPacket()).getY() + 0.3);
        }
    }
}
