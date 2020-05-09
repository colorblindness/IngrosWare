package best.reich.ingros.module.modules.movement;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.events.other.BoundingBoxEvent;
import best.reich.ingros.events.other.OpaqueEvent;
import best.reich.ingros.events.other.PushOutOfBlocksEvent;
import best.reich.ingros.mixin.accessors.IPlayerSP;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Mode;
import me.xenforu.kelo.setting.annotation.Setting;
import me.xenforu.kelo.util.math.MathUtil;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

@ModuleManifest(label = "Phase", category = ModuleCategory.MOVEMENT, color = 0x9EB0B8)
public class Phase extends ToggleableModule {
    @Setting("Mode")
    @Mode({"SAND", "PACKET", "SKIP", "NOCLIP", "VCLIP", "FULL"})
    public String mode = "PACKET";
    @Setting("Floor")
    public boolean floor = true;
    private boolean zoomies = true;

    @Subscribe
    public void onOpaqueBlock(OpaqueEvent event) {
        event.setCancelled(true);
    }

    @Subscribe
    public void onPush(PushOutOfBlocksEvent event) {
        if (mode.toUpperCase().equals("FULL")) {
            if (!mc.player.isCollidedHorizontally && mc.player.hurtTime != 0 || mc.player.isSneaking()) {
                event.setCancelled(true);
            }
        } else event.setCancelled(true);
    }

    @Subscribe
    public void collideWithBlock(BoundingBoxEvent event) {
        if (mc.player != null) {
            final boolean f = !floor || event.getPos().getY() >= 1;
            if (mode.toUpperCase().equals("VCLIP")) {
                if (!zoomies)
                    event.setAabb(null);
            }
            if (mode.toUpperCase().equals("SAND")) {
                if (mc.player.getRidingEntity() != null && event.getEntity() == mc.player.getRidingEntity()) {
                    if (mc.gameSettings.keyBindSprint.isKeyDown() && f) {
                        event.setCancelled(true);
                    } else {
                        if (mc.gameSettings.keyBindJump.isKeyDown() && event.getPos().getY() >= mc.player.getRidingEntity().posY) {
                            event.setCancelled(true);
                        }
                        if (event.getPos().getY() >= mc.player.getRidingEntity().posY) {
                            event.setCancelled(true);
                        }
                    }
                } else if (event.getEntity() == mc.player) {
                    if (mc.gameSettings.keyBindSneak.isKeyDown() && f) {
                        event.setCancelled(true);
                    } else {
                        if (mc.gameSettings.keyBindJump.isKeyDown() && event.getPos().getY() >= mc.player.posY) {
                            event.setCancelled(true);
                        }
                        if (event.getPos().getY() >= mc.player.posY) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
            if (mode.toUpperCase().equals("NOCLIP")) {
                if (event.getEntity() == mc.player || mc.player.getRidingEntity() != null && event.getEntity() == mc.player.getRidingEntity()) {
                    event.setCancelled(true);
                }
            }
            if (mode.toUpperCase().equals("FULL")) {
                if (!mc.player.isCollidedHorizontally) {
                    if (event.getBoundingBox() != null && event.getBoundingBox().minY >= mc.player.posY) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @Subscribe
    public void sendPacket(PacketEvent event) {
        if (event.getType() == EventType.PRE) {
            if (mode.toUpperCase().equals("NOCLIP")) {
                if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == EventType.PRE) {
            if (mode.toUpperCase().equals("FULL")) {
                if (((IPlayerSP)mc.player).isMoving()) {
                    mc.player.setSprinting(false);
                    Vec2f vec = getMoveVector();
                    if (mc.player.isCollidedHorizontally) { // THIS pushes you into the block
                        double x = vec.x * 0.006;
                        double z = vec.y * 0.006;
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + x,
                                mc.player.posY, mc.player.posZ + z, true));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + x * 11,
                                mc.player.posY + 301, mc.player.posZ + z * 11, false));
                        mc.player.setPosition(mc.player.posX + x, mc.player.posY, mc.player.posZ + z);
                    } else if (mc.player.hurtTime == 9 || mc.player.isSneaking()) { // THIS sends you through the block
                        double px = vec.x * 0.5;
                        double pz = vec.y * 0.5;
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + px,
                                mc.player.posY, mc.player.posZ + pz, true));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + px * 11,
                                mc.player.posY + 301, mc.player.posZ + pz * 11, false));
                        mc.player.setPosition(mc.player.posX + px, mc.player.posY, mc.player.posZ + pz);
                    }
                } else if (mc.player.isSneaking()) { // DownClip because NCP is broken
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,
                            mc.player.posY - 1, mc.player.posZ, true));
                } else if (mc.gameSettings.keyBindJump.isKeyDown()) { // DownClip but for when you're stuck
                    mc.player.setPosition(mc.player.posX, mc.player.posY - 0.5, mc.player.posZ);
                }
            }
            if (mode.toUpperCase().equals("VCLIP")) {
                final double[] dirSpeed = MathUtil.directionSpeed((zoomies ? 0.0225f : 0.0224f));
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + dirSpeed[0], mc.player.posY + (mc.gameSettings.keyBindJump.isKeyDown() ? (zoomies ? 0.0625 : 0.0624) : 0.00000001) - (mc.gameSettings.keyBindSneak.isKeyDown() ? (zoomies ? 0.0625 : 0.0624) : 0.00000002), mc.player.posZ + dirSpeed[1], mc.player.rotationYaw, mc.player.rotationPitch, false));
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, -1337, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                mc.player.setPosition(mc.player.posX + dirSpeed[0], mc.player.posY + (mc.gameSettings.keyBindJump.isKeyDown() ? (zoomies ? 0.0625 : 0.0624) : 0.00000001) - (mc.gameSettings.keyBindSneak.isKeyDown() ? (zoomies ? 0.0625 : 0.0624) : 0.00000002), mc.player.posZ + dirSpeed[1]);
                zoomies = !zoomies;
                mc.player.motionX = mc.player.motionY = mc.player.motionZ = 0;
                mc.player.noClip = zoomies;
            }
            if (mode.toUpperCase().equals("NOCLIP")) {
                mc.player.setVelocity(0, 0, 0);
                if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
                    final double[] speed = MathUtil.directionSpeed(0.06f);
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + speed[0], mc.player.posY, mc.player.posZ + speed[1], mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, 0, mc.player.posZ, mc.player.onGround));
                }
                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.06f, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, 0, mc.player.posZ, mc.player.onGround));
                }

                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.06f, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, 0, mc.player.posZ, mc.player.onGround));
                }
            }
        } else {
            if (mode.toUpperCase().equals("SAND")) {
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    if (mc.player.getRidingEntity() != null && mc.player.getRidingEntity() instanceof EntityBoat) {
                        final EntityBoat boat = (EntityBoat) mc.player.getRidingEntity();
                        if (boat.onGround) {
                            boat.motionY = 0.42f;
                        }
                    }
                }
            }

            if (mode.toUpperCase().equals("PACKET")) {
                final Vec3d dir = MathUtil.direction(mc.player.rotationYaw);
                if (dir != null) {
                    if (mc.player.onGround && mc.player.isCollidedHorizontally) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir.x * 0.00001f, mc.player.posY, mc.player.posZ + dir.z * 0.0001f, mc.player.onGround));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir.x * 2.0f, mc.player.posY, mc.player.posZ + dir.z * 2.0f, mc.player.onGround));
                    }
                }
            }

            if (mode.toUpperCase().equals("SKIP")) {
                final Vec3d dir = MathUtil.direction(mc.player.rotationYaw);
                if (dir != null) {
                    if (mc.player.onGround && mc.player.isCollidedHorizontally) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir.x * 0.001f, mc.player.posY + 0.1f, mc.player.posZ + dir.z * 0.001f, mc.player.onGround));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir.x * 0.03f, 0, mc.player.posZ + dir.z * 0.03f, mc.player.onGround));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir.x * 0.06f, mc.player.posY, mc.player.posZ + dir.z * 0.06f, mc.player.onGround));
                    }
                }
            }
        }
    }

    private Vec2f getMoveVector() {
        float yaw = mc.player.rotationYawHead;
        float forward = mc.player.movementInput.moveForward;
        float strafing = mc.player.movementInput.moveStrafe;

        float dir = forward < 0 ? -0.5f : forward > 0 ? 0.5f : 1f;

        if (strafing > 0) {
            yaw -= 90f * dir;
        } else if (strafing < 0) {
            yaw += 90f * dir;
        }

        if (forward < 0) {
            yaw += 180f;
        }

        yaw *= (float) (Math.PI / 180f);
        return new Vec2f(-MathHelper.sin(yaw), MathHelper.cos(yaw));
    }
}