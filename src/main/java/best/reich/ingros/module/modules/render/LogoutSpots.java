package best.reich.ingros.module.modules.render;

import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.events.other.WorldLoadEvent;
import best.reich.ingros.events.render.Render3DEvent;
import best.reich.ingros.mixin.accessors.IRenderManager;
import best.reich.ingros.util.render.RenderUtil;
import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@ModuleManifest(label = "LogoutSpots", category = ModuleCategory.RENDER, color = 0xffff0f70,hidden = true)
public class LogoutSpots extends ToggleableModule {
    private final ArrayList<logoutPos> logoutPositions = new ArrayList<>();

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (event.getType() == EventType.POST) {
            if (event.getPacket() instanceof SPacketPlayerListItem) {
                SPacketPlayerListItem playerListPacket = (SPacketPlayerListItem) event.getPacket();
                if (playerListPacket.getAction().equals(SPacketPlayerListItem.Action.REMOVE_PLAYER) || playerListPacket.getAction().equals(SPacketPlayerListItem.Action.ADD_PLAYER)) {
                    playerListPacket.getEntries()
                            .stream()
                            .filter(Objects::nonNull)
                            .filter(data -> {
                                String name = getNameFromComponent(data.getProfile());
                                return !Strings.isNullOrEmpty(name) && !isLocalPlayer(name) || playerListPacket.getAction().equals(SPacketPlayerListItem.Action.REMOVE_PLAYER);
                            })
                            .forEach(data -> {
                                UUID id = data.getProfile().getId();
                                switch (playerListPacket.getAction()) {
                                    case ADD_PLAYER:
                                        logoutPositions.removeIf(pos -> Objects.equals(id, pos.id));
                                        break;
                                    case REMOVE_PLAYER:
                                        if (mc.world.getPlayerEntityByUUID(id) != null) {
                                            EntityPlayer player = mc.world.getPlayerEntityByUUID(id);
                                            logoutPositions.add(new logoutPos(new Vec3d(player.posX, player.posY, player.posZ), id, player.getName()));
                                        }
                                        break;
                                }
                            });
                }
            }
        }
    }

    @Subscribe
    public void onWorldLoad(WorldLoadEvent event) {
        logoutPositions.clear();
    }

    @Subscribe
    public void onRender3D(Render3DEvent event) {
        if (mc.world == null || mc.player == null) return;
        for (logoutPos pos : logoutPositions) {
            final double posX = pos.pos.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
            final double posY = pos.pos.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
            final double posZ = pos.pos.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
            RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, 1, 2, 1).offset(posX - 0.5, posY, posZ - 0.5), 125, 120, 255, 40F);
            RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, 1, 2, 1).offset(posX - 0.5, posY, posZ - 0.5), 125, 120, 255, 255f, 1f);
            RenderUtil.renderTag(pos.name, posX, posY + 0.5, posZ, new Color(125, 120, 255).getRGB());
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderHelper.disableStandardItemLighting();
        }
    }

    private String getNameFromComponent(GameProfile profile) {
        return Objects.nonNull(profile) ? profile.getName() : "";
    }

    private boolean isLocalPlayer(String username) {
        return Objects.nonNull(mc.player) && mc.player.getDisplayName().getUnformattedText().equals(username);
    }

    public class logoutPos {
        Vec3d pos;
        UUID id;
        String name;

        public logoutPos(Vec3d position, UUID uuid, String name) {
            this.pos = position;
            this.id = uuid;
            this.name = name;
        }
    }
}