package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.entity.MotionEvent;
import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.events.other.BoundingBoxEvent;
import best.reich.ingros.events.other.PushOutOfBlocksEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.*;

import java.util.Objects;

@ModuleManifest(label = "Freecam", category = ModuleCategory.OTHER, color = 0xffff00ff)
public class Freecam extends ToggleableModule {
    private double x,y,z,yaw,pitch;
    @Override
    public void onEnable() {
        if (Objects.nonNull(mc.world)) {
            this.x = mc.player.posX;
            this.y = mc.player.posY;
            this.z = mc.player.posZ;
            this.yaw = mc.player.rotationYaw;
            this.pitch = mc.player.rotationPitch;
            final EntityOtherPlayerMP entityOtherPlayerMP = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
            entityOtherPlayerMP.inventory = mc.player.inventory;
            entityOtherPlayerMP.inventoryContainer = mc.player.inventoryContainer;
            entityOtherPlayerMP.setPositionAndRotation(this.x, mc.player.getEntityBoundingBox().minY, this.z, mc.player.rotationYaw, mc.player.rotationPitch);
            entityOtherPlayerMP.rotationYawHead = mc.player.rotationYawHead;
            entityOtherPlayerMP.setSneaking(mc.player.isSneaking());
            mc.world.addEntityToWorld(-1488, entityOtherPlayerMP);
        }
    }

    @Override
    public void onDisable() {
        if (Objects.nonNull(mc.world)) {
            mc.player.jumpMovementFactor = 0.02f;
            mc.player.setPosition(this.x, this.y, this.z);
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.01, mc.player.posZ, mc.player.onGround));
            mc.player.noClip = false;
            mc.world.removeEntityFromWorld(-1488);
            mc.player.motionY = 0.0;
            mc.player.rotationPitch = (float) pitch;
            mc.player.rotationYaw = (float) yaw;
            yaw = pitch = 0;
        }
        mc.renderGlobal.loadRenderers();
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == EventType.PRE) {
            mc.player.setVelocity(0.0, 0.0, 0.0);
            mc.player.jumpMovementFactor = 1;
            if (mc.currentScreen == null) {
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
                    mc.player.motionY += 1;
                }
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.player.motionY -= 1;
                }
            }
            mc.player.noClip = true;
            mc.player.renderArmPitch = 5000.0f;
        }
    }

    @Subscribe
    public void onMotion(MotionEvent event) {
        setMoveSpeed(event, 1);
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && !GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
            event.setY(2.0 * -(mc.player.rotationPitch / 180.0f) * mc.player.movementInput.moveForward);
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getType() == EventType.PRE) {
            if (event.getPacket() instanceof CPacketPlayer) event.setCancelled(true);
            if (event.getPacket() instanceof CPacketPlayerDigging || event.getPacket() instanceof CPacketEntityAction || event.getPacket() instanceof CPacketUseEntity || event.getPacket() instanceof CPacketAnimation)
                event.setCancelled(true);
        }
    }

    @Subscribe
    public void onBB(BoundingBoxEvent event) {
        event.setAabb(null);
    }

    @Subscribe
    public void onPush(PushOutOfBlocksEvent event) {
        event.setCancelled(true);
    }

    private void setMoveSpeed(MotionEvent event, double speed) {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            event.setX(forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw)));
            event.setZ(forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw)));
        }
    }

}
