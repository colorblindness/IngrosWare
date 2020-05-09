package best.reich.ingros.module.modules.movement;

import best.reich.ingros.events.entity.MotionEvent;
import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.mixin.accessors.ISPacketPosLook;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketPlayerPosLook;


@ModuleManifest(label = "ElytraFly", category = ModuleCategory.MOVEMENT, color = 0xffFF33fa)
public class ElytraFly extends ToggleableModule {
    @Clamp(maximum = "10.0")
    @Setting("HorizontalSpeed")
    public float hspeed = 1.4F;

    @Clamp(maximum = "10.0")
    @Setting("VerticalSpeed")
    public float vspeed = 0.4F;

    @Subscribe
    private void onRecieve(PacketEvent event) {
        if (mc.player == null || !mc.player.isElytraFlying()) return;
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            ISPacketPosLook packet = (ISPacketPosLook) event.getPacket();
            packet.setPitch(mc.player.rotationPitch);
        }
    }

    @Subscribe
    public void onMotion(MotionEvent event) {
        if (mc.player == null) return;
        ItemStack itemstack = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (itemstack.getItem() == Items.ELYTRA && ItemElytra.isUsable(itemstack)) {
            if (mc.player.isElytraFlying()) {
                event.setY(mc.gameSettings.keyBindJump.isKeyDown() ? vspeed : mc.gameSettings.keyBindSneak.isKeyDown() ? -vspeed : 0);
                mc.player.addVelocity(0, mc.gameSettings.keyBindJump.isKeyDown() ? vspeed : mc.gameSettings.keyBindSneak.isKeyDown() ? -vspeed : 0, 0);
                mc.player.rotateElytraX = 0;
                mc.player.rotateElytraY = 0;
                mc.player.rotateElytraZ = 0;
                mc.player.moveVertical = mc.gameSettings.keyBindJump.isKeyDown() ? vspeed : mc.gameSettings.keyBindSneak.isKeyDown() ? -vspeed : 0;
                double forward = mc.player.movementInput.moveForward;
                double strafe = mc.player.movementInput.moveStrafe;
                float yaw = mc.player.rotationYaw;
                if ((forward == 0.0D) && (strafe == 0.0D)) {
                    event.setX(0);
                    event.setZ(0);
                } else {
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
                    final double cos = Math.cos(Math.toRadians(yaw + 90.0F));
                    final double sin = Math.sin(Math.toRadians(yaw + 90.0F));
                    event.setX((forward * hspeed * cos + strafe * hspeed * sin));
                    event.setZ((forward * hspeed * sin - strafe * hspeed * cos));
                }
            }
        }
    }
}