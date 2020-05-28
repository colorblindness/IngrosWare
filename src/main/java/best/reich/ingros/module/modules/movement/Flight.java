package best.reich.ingros.module.modules.movement;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.mixin.accessors.IPlayerSP;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Mode;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

@ModuleManifest(label = "Flight", category = ModuleCategory.MOVEMENT, color = 0x3B7A91)
public class Flight extends ToggleableModule {
    @Setting("mode")
    @Mode({"CREATIVE", "PACKET", "VANILLA"})
    public String mode = "CREATIVE";
    @Clamp(minimum = "0.01", maximum = "10.0")
    @Setting("Speed")
    public double speed = 0.06;
    private boolean zoomies;
    private int teleportId;
    private final List<CPacketPlayer> packets = new ArrayList<>();
    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;
        setSuffix(mode);
        switch (mode.toUpperCase()) {
            case "CREATIVE":
                mc.player.capabilities.isFlying = true;
                break;
            case "VANILLA":
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.motionY = speed;
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.motionY = -speed;
                } else {
                    mc.player.motionY = 0;
                }
                double[] direction = directionSpeed(speed);
                mc.player.motionX = direction[0];
                mc.player.motionZ = direction[1];
                break;
            case "PACKET":
                if (this.teleportId <= 0) {
                    final CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX, 1, mc.player.posZ, mc.player.onGround);
                    this.packets.add(bounds);
                    mc.player.connection.sendPacket(bounds);
                    return;
                }
                mc.player.setVelocity(0,0,0);
                double posY = mc.gameSettings.keyBindSneak.isKeyDown() ? (zoomies ? -0.0325 : -0.0324):(mc.gameSettings.keyBindJump.isKeyDown() ? (zoomies ? 0.0625 : 0.0624) : (mc.player.ticksExisted % 10 == 0 ? -0.0344f:-0.01f));
                if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                    if (((IPlayerSP) mc.player).isMoving()) {
                        for (double x = 0.0625; x < this.speed; x += 0.262) {
                            final double[] dir = directionSpeed(x);
                            mc.player.setVelocity(dir[0], posY, dir[1]);
                            move(dir[0], posY, dir[1]);
                        }
                    }
                } else {
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        for (int i = 0; i <= 3; i++) {
                            mc.player.setVelocity(0, posY * i, 0);
                            move(0, posY * i, 0);
                        }
                    } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        for (int i = 0; i <= 3; i++) {
                            mc.player.setVelocity(0, posY * i, 0);
                            move(0, posY * i, 0);
                        }
                    }
                }
                zoomies = !zoomies;
                break;
        }
    }

    @Subscribe
    public void onSend(PacketEvent event) {
        if (mc.player == null) return;
        switch (event.getType()) {
            case PRE:
                if (mode.toUpperCase().equals("PACKET")) {
                    if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position)) {
                        event.setCancelled(true);
                    }
                    if (event.getPacket() instanceof CPacketPlayer) {
                        final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                        if (packets.contains(packet)) {
                            packets.remove(packet);
                            return;
                        }
                        event.setCancelled(true);
                    }
                }
                break;
            case POST:
                if (mode.toUpperCase().equals("PACKET")) {
                    if (event.getPacket() instanceof SPacketPlayerPosLook) {
                        final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
                        if (mc.player.isEntityAlive() && mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)) && !(mc.currentScreen instanceof GuiDownloadTerrain)) {
                            if (this.teleportId <= 0) {
                                this.teleportId = packet.getTeleportId();
                            } else {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mode.toUpperCase().equals("PACKET") && mc.world != null) {
            this.teleportId = 0;
            this.packets.clear();
            final CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX, 1, mc.player.posZ, mc.player.onGround);
            this.packets.add(bounds);
            mc.player.connection.sendPacket(bounds);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mode.toUpperCase().equals("CREATIVE") && mc.player != null) {
            mc.player.capabilities.isFlying = false;
        }
    }

    private void move(double x, double y, double z) {
        final CPacketPlayer pos = new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z, mc.player.onGround);
        this.packets.add(pos);
        mc.player.connection.sendPacket(pos);

        final CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX + x, 1, mc.player.posZ + z, mc.player.onGround);
        this.packets.add(bounds);
        mc.player.connection.sendPacket(bounds);

        this.teleportId++;
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId - 1));
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId));
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId + 1));
    }

    private double[] directionSpeed(double speed) {
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

        if (moveForward != 0) {
            if (moveStrafe > 0) {
                rotationYaw += (moveForward > 0 ? -45 : 45);
            } else if (moveStrafe < 0) {
                rotationYaw += (moveForward > 0 ? 45 : -45);
            }
            moveStrafe = 0;
            if (moveForward > 0) {
                moveForward = 1;
            } else if (moveForward < 0) {
                moveForward = -1;
            }
        }

        double posX = (moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw)));
        double posZ = (moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw)));

        return new double[]{posX, posZ};
    }
}
