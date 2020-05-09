package best.reich.ingros.module.modules.render;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.render.Render2DEvent;
import best.reich.ingros.module.persistent.Overlay;
import best.reich.ingros.util.render.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

@ModuleManifest(label = "PlayerList", category = ModuleCategory.RENDER, color = 0xFF6AEF66)
public class PlayerList extends ToggleableModule {
    @Setting("ShowFriends")
    public boolean showFriends = true;

    @Subscribe
    public void onRender2D(Render2DEvent event) {
        if (mc.world == null || mc.player == null) return;
        final Overlay overlay = (Overlay) IngrosWare.INSTANCE.moduleManager.getModule("Overlay");
        float offset = 0;
        final ArrayList<EntityPlayer> players = new ArrayList<>();
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player == mc.player || !player.isEntityAlive() || player.getEntityId() == -1488 || (!showFriends && IngrosWare.INSTANCE.friendManager.isFriend(player.getName())))
                continue;
            players.add(player);
        }
        players.sort(Comparator.comparingDouble(player->mc.player.getDistanceToEntity(player)));
        for (EntityPlayer player : players) {
            RenderUtil.drawText(Math.floor(player.getHealth() + player.getAbsorptionAmount()) + " " + (IngrosWare.INSTANCE.friendManager.isFriend(player.getName()) ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY) + player.getName() + " " + ChatFormatting.GRAY + (int) mc.player.getDistanceToEntity(player), 2, event.getScaledResolution().getScaledHeight() / 2 + offset  - ((players.size() * (RenderUtil.getTextHeight(overlay.font) + 2)) / 2), getHealthColor(player), overlay.font);
            offset += RenderUtil.getTextHeight(overlay.font) + 2;
        }
    }

    private int getHealthColor(EntityLivingBase player) {
        return Color.HSBtoRGB(Math.max(0.0F, Math.min(player.getHealth(), player.getMaxHealth()) / player.getMaxHealth()) / 3.0F, 1.0F, 0.8f) | 0xFF000000;
    }
}
