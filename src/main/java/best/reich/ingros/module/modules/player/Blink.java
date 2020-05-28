package best.reich.ingros.module.modules.player;

import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.events.render.Render3DEvent;
import best.reich.ingros.mixin.accessors.IRenderManager;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3d;
import java.util.ArrayList;


@ModuleManifest(label = "Blink", category = ModuleCategory.PLAYER, color = 0xffffEA10)
public class Blink extends ToggleableModule {
    private ArrayList<Packet> packets = new ArrayList<>();
    private ArrayList<Vector3d> locations = new ArrayList<>();

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.PRE) {
            if (hasMoved()) {
                locations.add(new Vector3d(mc.player.posX, mc.player.posY, mc.player.posZ));
            }
            packets.add(event.getPacket());
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        if (mc.player == null) return;
        if (!locations.isEmpty()) {
            GL11.glPushMatrix();
            GL11.glLineWidth(3);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor3d(1, 1, 1);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            for (Vector3d vector : locations) {
                GL11.glVertex3d(vector.x - ((IRenderManager)mc.getRenderManager()).getRenderPosX(),
                        vector.y - ((IRenderManager)mc.getRenderManager()).getRenderPosY(),
                        vector.z - ((IRenderManager)mc.getRenderManager()).getRenderPosZ());
            }
            GL11.glEnd();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopMatrix();
        }
    }

    @Override
    public void onEnable() {
        if (mc.world == null) return;
        final EntityOtherPlayerMP entityOtherPlayerMP = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
        entityOtherPlayerMP.inventory = mc.player.inventory;
        entityOtherPlayerMP.inventoryContainer = mc.player.inventoryContainer;
        entityOtherPlayerMP.setPositionAndRotation(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
        entityOtherPlayerMP.rotationYawHead = mc.player.rotationYawHead;
        entityOtherPlayerMP.setSneaking(mc.player.isSneaking());
        mc.world.addEntityToWorld(-13376969, entityOtherPlayerMP);
        packets.clear();
    }

    @Override
    public void onDisable() {
        if (mc.world == null) return;
        mc.world.removeEntityFromWorld(-13376969);
        packets.forEach(mc.player.connection.getNetworkManager()::sendPacket);
        mc.player.motionY = 0.02;
        packets.clear();
        locations.clear();
    }

    private boolean hasMoved() {
        return mc.player.posX != mc.player.prevPosX || mc.player.posY != mc.player.prevPosY || mc.player.posZ != mc.player.prevPosZ;
    }
}