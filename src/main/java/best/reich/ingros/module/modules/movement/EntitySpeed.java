package best.reich.ingros.module.modules.movement;

import best.reich.ingros.events.entity.MotionEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityLlama;
import org.lwjgl.input.Keyboard;


@ModuleManifest(label = "EntitySpeed", category = ModuleCategory.MOVEMENT, color = 0xffAE33fa)
public class EntitySpeed extends ToggleableModule {
    @Setting("Flight")
    public boolean flight = false;
    @Clamp(maximum = "10.0")
    @Setting("StepHeight")
    public float stepHeight = 1.0F;
    @Clamp(maximum = "10.0")
    @Setting("Speed")
    public float speed = 1.4F;

    @Subscribe
    public void onMotion(MotionEvent event) {
        if (mc.player == null) return;
        if (mc.player.isRiding()) {
            mc.player.getRidingEntity().stepHeight =stepHeight;
            if (flight) {
                if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)) {
                    mc.player.getRidingEntity().motionY = speed;
                } else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5)) {
                    mc.player.getRidingEntity().motionY = -speed;
                } else {
                    mc.player.getRidingEntity().motionY = 0.0;
                }
            }
            if (mc.player.getRidingEntity() instanceof EntityLlama) {
                mc.player.getRidingEntity().rotationYaw = mc.player.rotationYaw;
                ((EntityLlama) mc.player.getRidingEntity()).rotationYawHead = mc.player.rotationYawHead;
            }
            double forward = mc.player.movementInput.moveForward;
            double strafe = mc.player.movementInput.moveStrafe;
            float yaw = mc.player.rotationYaw;
            if ((forward == 0.0D) && (strafe == 0.0D)) {
                mc.player.getRidingEntity().motionX = 0.0D;
                mc.player.getRidingEntity().motionZ = 0.0D;
            }
            else {
                if (forward != 0.0D) {
                    if (strafe > 0.0D) {
                        yaw += (forward > 0.0D ? -45 : 45);
                    } else if (strafe < 0.0D) {
                        yaw += (forward > 0.0D ? 45 : -45);
                    }
                    strafe = 0.0D;
                    if (forward > 0.0D) {
                        forward = 1.0D;
                    } else if (forward < 0.0D) {
                        forward = -1.0D;
                    }
                }
                mc.player.getRidingEntity().motionX = (forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
                mc.player.getRidingEntity().motionZ = (forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
                if (mc.player.getRidingEntity() instanceof EntityMinecart) {
                    final EntityMinecart em = (EntityMinecart) mc.player.getRidingEntity();
                    em.setVelocity((forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F))), em.motionY, (forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F))));
                }
            }
        }
    }
    
    @Override
    public void onDisable() {
        if (mc.player.isRiding()) {
            mc.player.getRidingEntity().stepHeight = 1.0f;
        }
    }
}