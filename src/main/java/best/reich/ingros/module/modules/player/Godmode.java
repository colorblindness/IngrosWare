package best.reich.ingros.module.modules.player;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.network.PacketEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;

@ModuleManifest(label = "Godmode", category = ModuleCategory.PLAYER, color = 0xff40AE70)
public class Godmode extends ToggleableModule {
    @Setting("Remount")
    public boolean remount = true;

    public Entity entity;
    @Override
    public void onEnable() {
        if (mc.world != null && mc.player.getRidingEntity() != null) {
            entity = mc.player.getRidingEntity();
            mc.renderGlobal.loadRenderers();
            hideEntity();
            mc.player.setPosition(mc.player.getPosition().getX(),
                    mc.player.getPosition().getY() - 1,
                    mc.player.getPosition().getZ());
        }
        if (mc.world != null && remount) {
            remount = false;
        }
    }
    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) return;
        if (remount) {
            remount = false;
        }
        mc.player.dismountRidingEntity();
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        if (entity != null) {
            entity.isDead = false;
            mc.world.loadedEntityList.add(entity);
        }
    }

    @Subscribe
    public void onPacketSend(PacketEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (event.getType() == EventType.PRE) {
            if (event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
                event.setCancelled(true);
            }
        }
    }

    private void hideEntity() {
        if (mc.player.getRidingEntity() != null) {
            mc.player.dismountRidingEntity();
            mc.world.removeEntity(entity);
        }
    }

    private void showEntity(Entity entity2) {
        entity2.isDead = false;
        mc.world.loadedEntityList.add(entity2);
        mc.player.startRiding(entity2, true);
    }

    @Subscribe
    public void onPlayerWalkingUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (event.getType() == EventType.PRE) {
            if (mc.player.isRiding()) {
                if (remount) {
                    showEntity(entity);
                }
                entity.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
                mc.player.connection.sendPacket(new CPacketVehicleMove(entity));
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, true));
                mc.player.connection.sendPacket(new CPacketInput(mc.player.movementInput.moveStrafe, mc.player.movementInput.moveForward, false, false));
            }
        }
    }
}