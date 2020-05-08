package best.reich.ingros.module.modules.movement;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.MotionEvent;
import best.reich.ingros.module.modules.movement.Flight;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.minecraft.init.MobEffects;

import java.util.Objects;


@ModuleManifest(label = "Strafe", category = ModuleCategory.MOVEMENT, color = 0xff0AEf00)
public class Strafe extends ToggleableModule {
    @Setting("Ground")
    public boolean ground = false;
    @Subscribe
    public void onMotion(MotionEvent event) {
        if (mc.player == null)
            return;

        if (mc.player.isSneaking() || mc.player.isOnLadder() || mc.player.isInWeb || mc.player.isInLava() || mc.player.isInWater() || mc.player.capabilities.isFlying)
            return;

        if (!this.ground) {
            if (mc.player.onGround)
                return;
        }

        final Flight flightModule = (Flight) IngrosWare.INSTANCE.moduleManager.getModule("Flight");
        if (flightModule != null && flightModule.isEnabled()) {
            return;
        }

        float playerSpeed = 0.2873f;
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.rotationYaw;

        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            playerSpeed *= (1.0f + 0.2f * (amplifier + 1));
        }

        if (moveForward == 0.0f && moveStrafe == 0.0f) {
            event.setX(0.0d);
            event.setZ(0.0d);
        } else {
            if (moveForward != 0.0f) {
                if (moveStrafe > 0.0f) {
                    rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
                } else if (moveStrafe < 0.0f) {
                    rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
                }
                moveStrafe = 0.0f;
                if (moveForward > 0.0f) {
                    moveForward = 1.0f;
                } else if (moveForward < 0.0f) {
                    moveForward = -1.0f;
                }
            }
            double sin = Math.sin(Math.toRadians((rotationYaw + 90.0f)));
            double cos = Math.cos(Math.toRadians((rotationYaw + 90.0f)));
            event.setX((moveForward * playerSpeed) * cos + (moveStrafe * playerSpeed) * sin);
            event.setZ((moveForward * playerSpeed) * sin - (moveStrafe * playerSpeed) * cos);
        }
    }
}
