package best.reich.ingros.module.modules.render;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.events.render.Render2DEvent;
import best.reich.ingros.mixin.accessors.IRenderManager;
import best.reich.ingros.util.logging.Logger;
import best.reich.ingros.util.render.GLUProjection;
import best.reich.ingros.util.render.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@ModuleManifest(label = "WaifuESP", category = ModuleCategory.RENDER, color = 0xff007777, hidden = false)
public class WaifuESP extends ToggleableModule {
    private ResourceLocation waifu;

    private final String waifuUrl = "https://i.imgur.com/AFZUcqA.png";


    private final File waifuCache =
            new File(IngrosWare.INSTANCE.path + File.separator + "waifu.png");

    private <T> BufferedImage getImage(T source, ThrowingFunction<T, BufferedImage> readFunction) {
        try {
            return readFunction.apply(source);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    @Subscribe
    public void onRenderGameOverlayEvent(Render2DEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (waifu == null) {
            return;
        }

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                final EntityLivingBase ent = (EntityLivingBase) entity;
                if (isValid(ent) && ent.getUniqueID() != mc.player.getUniqueID() && RenderUtil.isInViewFrustrum(ent)) {

                    float partialTicks = event.getPartialTicks();
                    double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks();
                    double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.getPartialTicks();
                    double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks();
                    final AxisAlignedBB bb = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1);
                    final Vector3d[] corners = {new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f)};
                    GLUProjection.Projection result;
                    final Vector4f transformed = new Vector4f(event.getScaledResolution().getScaledWidth() * 2.0f, event.getScaledResolution().getScaledHeight() * 2.0f, -1.0f, -1.0f);
                    for (Vector3d vec : corners) {
                        result = GLUProjection.getInstance().project(vec.x - mc.getRenderManager().viewerPosX, vec.y - mc.getRenderManager().viewerPosY, vec.z - mc.getRenderManager().viewerPosZ, GLUProjection.ClampMode.NONE, true);
                        transformed.setX((float) Math.min(transformed.getX(), result.getX()));
                        transformed.setY((float) Math.min(transformed.getY(), result.getY()));
                        transformed.setW((float) Math.max(transformed.getW(), result.getX()));
                        transformed.setZ((float) Math.max(transformed.getZ(), result.getY()));
                    }
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(.5f, .5f, .5f);
                    final float x = transformed.x * 2;
                    final float w = (transformed.w * 2) - x;
                    final float y = transformed.y * 2;
                    final float h = (transformed.z * 2) - y;


                    mc.renderEngine.bindTexture(waifu);

                    GlStateManager.color(255, 255, 255);
                    Gui.drawScaledCustomSizeModalRect(
                            (int)x, (int)y, 0, 0, (int)w, (int)h, (int)w, (int)h, w, h);
                    GlStateManager.scale(1.0f, 1.0f, 1.0f);
                    GlStateManager.popMatrix();

                }
            }
        }
    }

    public void onEnable() {
        mc.addScheduledTask(
                () -> {
                    try {
                        BufferedImage image;
                        if (waifuCache.exists()) { // TODO: download async
                            image = getImage(waifuCache, ImageIO::read); // from cache
                        } else {
                            image = getImage(new URL(waifuUrl), ImageIO::read); // from internet
                            if (image != null) {
                                try {
                                    ImageIO.write(image, "png", waifuCache);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        if (image == null) {
                            Logger.printMessage("Failed to download waifu image", true);
                            return;
                        }

                        DynamicTexture dynamicTexture = new DynamicTexture(image);
                        dynamicTexture.loadTexture(mc.getResourceManager());
                        waifu = mc.getTextureManager().getDynamicTextureLocation("WAIFU", dynamicTexture);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.printMessage("WaifuESP did an oopsie", true);
                    }
                });
    }


    @FunctionalInterface
    private interface ThrowingFunction<T, R> {

        R apply(T obj) throws IOException;
    }


    private boolean isValid(EntityLivingBase entity) {
        return mc.player != entity && entity.getEntityId() != -1488 && isValidType(entity) && entity.isEntityAlive();
}

    private boolean isValidType(EntityLivingBase entity) {
        return (entity instanceof EntityPlayer) || (((entity instanceof EntityMob || entity instanceof EntitySlime)) || ((entity instanceof EntityVillager || entity instanceof EntityGolem)) || (entity instanceof IAnimals));
    }

}
