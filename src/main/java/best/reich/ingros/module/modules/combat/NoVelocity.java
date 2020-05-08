package best.reich.ingros.module.modules.combat;

import best.reich.ingros.events.entity.EntityEvent;
import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.network.PacketEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 *
 * Updated a bit. -Zim 5/5/20
 **/
@ModuleManifest(label = "NoVelocity", category = ModuleCategory.COMBAT, color = 0x717171)
public class NoVelocity extends ToggleableModule {
    private boolean once;

    @Setting("AAC")
    public boolean aac;

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.world == null || mc.player == null) return;
        setSuffix(aac ? "AAC":"NCP");
        if(event.getType() == EventType.PRE) {
            if (aac) {
                if (mc.player.hurtTime == 9 & once & mc.player.onGround) {
                    once = false;
                }
                if (mc.player.hurtTime > 0 & mc.player.hurtTime <= 7) {
                    mc.player.motionX *= 0.5;
                    mc.player.motionZ *= 0.5;
                }
                if (mc.player.hurtTime == 5) {
                    mc.player.motionX = 0.0;
                    mc.player.motionZ = 0.0;
                    once = true;
                }
                if (mc.player.hurtTime == 4) {
                    final double playerYaw = Math.toRadians(mc.player.rotationYaw);
                    mc.player.setPosition(mc.player.posX - (Math.sin(playerYaw) * 0.05), mc.player.posY, mc.player.posZ);
                }
            }
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        if(event.getType() == EventType.POST) {
            if (!aac && mc.player != null) {
                if ((event.getPacket() instanceof SPacketEntityVelocity) && (((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId())) {
                    event.setCancelled(true);
                }
                if (event.getPacket() instanceof SPacketExplosion) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Subscribe
    public void entityCollision(EntityEvent.EntityCollision event){
        if (!aac && event.getEntity() == mc.player) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        once = false;
    }

}
