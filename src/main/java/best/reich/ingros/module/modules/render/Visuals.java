package best.reich.ingros.module.modules.render;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.render.Render2DEvent;
import best.reich.ingros.events.render.Render3DEvent;
import best.reich.ingros.events.render.RenderEntityEvent;
import best.reich.ingros.events.render.RenderNameEvent;
import best.reich.ingros.mixin.accessors.IEntityRenderer;
import best.reich.ingros.mixin.accessors.IRenderManager;
import best.reich.ingros.util.render.GLUProjection;
import best.reich.ingros.util.render.OutlineUtil;
import best.reich.ingros.util.render.RenderUtil;
import com.google.common.collect.ImmutableMap;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Mode;
import me.xenforu.kelo.setting.annotation.Setting;
import me.xenforu.kelo.util.math.MathUtil;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.block.BlockChest;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;
import java.awt.*;
import java.util.List;
import java.util.*;

@ModuleManifest(label = "Visuals", category = ModuleCategory.RENDER, color = 0xFF0030FF, hidden = true)
public class Visuals extends ToggleableModule {
    @Setting("PlayerColor")
    public Color playerColor = new Color(255, 0, 0);
    @Setting("Tracers")
    public boolean tracers = true;
    @Setting("BoundingBox")
    public boolean boundingbox = true;
    @Setting("BoxMode")
    @Mode({"2D", "3D", "BOTH"})
    public String boxmode = "2D";
    @Setting("Health")
    public boolean health = true;
    @Setting("Nametags")
    public boolean nametags = true;
    @Setting("Armor")
    public boolean armor = true;
    @Setting("Ping")
    public boolean ping = true;
    @Setting("Outline")
    public boolean outline = true;
    @Setting("Wallhack")
    public boolean wallHack = true;
    @Setting("Skeleton")
    public boolean skeleton = true;
    @Setting("Invisibles")
    public boolean invisibles = true;
    @Setting("Players")
    public boolean players = true;
    @Setting("Animals")
    public boolean animals;
    @Setting("Monsters")
    public boolean monsters;
    @Setting("Passives")
    public boolean passives;
    @Setting("Chests")
    public boolean chests = true;
    @Setting("Furnaces")
    public boolean furnaces;
    @Setting("Shulkers")
    public boolean shuklers = true;
    @Setting("EnchantTable")
    public boolean enchanttable;
    @Setting("BrewingStands")
    public boolean brewingStands;
    @Setting("RedstoneBlocks")
    public boolean redstoneBlocks;
    @Setting("Spawners")
    public boolean spawners;
    private final Map<EntityPlayer, float[][]> entities = new HashMap<>();
    private final ImmutableMap<String, String> cachedEnchantmentMap = new ImmutableMap.Builder<String, String>()
            .put(Objects.requireNonNull(Enchantment.getEnchantmentByID(0)).getName(), "p").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(10)).getName(), "cob").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(1)).getName(), "fp").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(2)).getName(), "ff").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(3)).getName(), "bp").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(4)).getName(), "pp").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(5)).getName(), "r").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(6)).getName(), "aa").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(7)).getName(), "t").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(8)).getName(), "ds").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(9)).getName(), "fw")
            .put(Objects.requireNonNull(Enchantment.getEnchantmentByID(16)).getName(), "s").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(22)).getName(), "se").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(17)).getName(), "sm").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(18)).getName(), "boa").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(19)).getName(), "kb").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(20)).getName(), "fa").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(21)).getName(), "l")
            .put(Objects.requireNonNull(Enchantment.getEnchantmentByID(32)).getName(), "e").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(33)).getName(), "st").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(35)).getName(), "f")
            .put(Objects.requireNonNull(Enchantment.getEnchantmentByID(48)).getName(), "pow").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(49)).getName(), "pun").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(50)).getName(), "fl").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(51)).getName(), "inf")
            .put(Objects.requireNonNull(Enchantment.getEnchantmentByID(61)).getName(), "lu").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(62)).getName(), "lots")
            .put(Objects.requireNonNull(Enchantment.getEnchantmentByID(34)).getName(), "un").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(70)).getName(), "m").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(71)).getName(), "vc").build();

    @Subscribe
    public void onRenderName(RenderNameEvent event) {
        if (nametags) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onRenderWorldToScreen(Render2DEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (armor || nametags || health || (boundingbox && (boxmode.equalsIgnoreCase("2D") || boxmode.equalsIgnoreCase("BOTH")))) {
            mc.world.loadedEntityList.forEach(entity -> {
                if (entity instanceof EntityLivingBase) {
                    final EntityLivingBase ent = (EntityLivingBase) entity;
                    if (isValid(ent) && ent.getUniqueID() != mc.player.getUniqueID() && RenderUtil.isInViewFrustrum(ent)) {
                        final Color clr = getEntityColor(entity);
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
                        GlStateManager.enableBlend();
                        GlStateManager.scale(.5f, .5f, .5f);
                        final float x = transformed.x * 2;
                        final float w = (transformed.w * 2) - x;
                        final float y = transformed.y * 2;
                        final float h = (transformed.z * 2) - y;
                        if (boundingbox && (boxmode.equalsIgnoreCase("2D") || boxmode.equalsIgnoreCase("BOTH"))) {
                            RenderUtil.drawBorderedRect(x, y, w, h, 1, 0x00000000, 0xff000000);
                            RenderUtil.drawBorderedRect(x - 1, y - 1, w + 2, h + 2, 1, 0x00000000, clr.getRGB());
                            RenderUtil.drawBorderedRect(x - 2, y - 2, w + 4, h + 4, 1, 0x00000000, 0xff000000);
                        }
                        if (health) {
                            final float height = (h / ent.getMaxHealth()) * Math.min(ent.getHealth(), ent.getMaxHealth());
                            RenderUtil.drawBorderedRect(x - 6, y - 1, 3, h + 2, 1, 0x20000000, 0xff000000);
                            RenderUtil.drawRect(x - 5, y + h, 1, -height, getHealthColor(ent));
                            if (ent.getMaxHealth() > ent.getHealth()) {
                                mc.fontRenderer.drawStringWithShadow((int) ent.getHealth() + "hp", x - 6 - mc.fontRenderer.getStringWidth((int) ent.getHealth() + "hp"), y + h - height, -1);
                            }
                        }
                        if (nametags) {
                            final NetworkPlayerInfo networkPlayerInfo = mc.getConnection().getPlayerInfo(ent.getUniqueID());
                            final String p = Objects.isNull(networkPlayerInfo) ? " 0ms" : " " + networkPlayerInfo.getResponseTime() + "ms";
                            final ChatFormatting healthColor = (Math.min((int) ent.getHealth() + (int) ent.getAbsorptionAmount(), 20) >= ent.getMaxHealth() / 1.45f ? ChatFormatting.GREEN : Math.min((int) ent.getHealth() + (int) ent.getAbsorptionAmount(), 20) >= ent.getMaxHealth() / 2f ? ChatFormatting.YELLOW : Math.min((int) ent.getHealth() + (int) ent.getAbsorptionAmount(), 20) >= ent.getMaxHealth() / 3f ? ChatFormatting.RED : ChatFormatting.DARK_RED);
                            final String str = (ping ? ChatFormatting.BLUE + p : "") + healthColor + " " + ((int) ent.getHealth() + (int) ent.getAbsorptionAmount());
                            RenderUtil.drawRect((x + (w / 2) - (mc.fontRenderer.getStringWidth((IngrosWare.INSTANCE.friendManager.isFriend(ent.getName()) ? (IngrosWare.INSTANCE.friendManager.getFriend(ent.getName()).getAlias() != null ? IngrosWare.INSTANCE.friendManager.getFriend(ent.getName()).getAlias() : ent.getName()) : ent.getName()) + str) >> 1)) - 2, y - 5 - mc.fontRenderer.FONT_HEIGHT, mc.fontRenderer.getStringWidth((IngrosWare.INSTANCE.friendManager.isFriend(ent.getName()) ? (IngrosWare.INSTANCE.friendManager.getFriend(ent.getName()).getAlias() != null ? IngrosWare.INSTANCE.friendManager.getFriend(ent.getName()).getAlias() : ent.getName()) : ent.getName()) + str) + 3, mc.fontRenderer.FONT_HEIGHT + 3, 0x60000000);
                            mc.fontRenderer.drawStringWithShadow((IngrosWare.INSTANCE.friendManager.isFriend(ent.getName()) ? (IngrosWare.INSTANCE.friendManager.getFriend(ent.getName()).getAlias() != null ? IngrosWare.INSTANCE.friendManager.getFriend(ent.getName()).getAlias() : ent.getName()) : ent.getName()) + str, (x + (w / 2) - (mc.fontRenderer.getStringWidth((IngrosWare.INSTANCE.friendManager.isFriend(ent.getName()) ? (IngrosWare.INSTANCE.friendManager.getFriend(ent.getName()).getAlias() != null ? IngrosWare.INSTANCE.friendManager.getFriend(ent.getName()).getAlias() : ent.getName()) : ent.getName()) + str) >> 1)), y - 3 - mc.fontRenderer.FONT_HEIGHT, clr.getRGB());
                        }
                        if (armor && ent instanceof EntityPlayer)
                            drawArmor((EntityPlayer) ent, (int) (x + w / 2), (int) (y - 1 - (mc.fontRenderer.FONT_HEIGHT * (nametags ? 3.15 : 2))));
                        GlStateManager.scale(1.0f, 1.0f, 1.0f);
                        GlStateManager.popMatrix();
                    }
                }
            });
        }
    }

    @Subscribe
    public void onPre(RenderEntityEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (event.getEntity() instanceof EntityLivingBase && isValid((EntityLivingBase) event.getEntity())) {
            if (event.getEventType() == EventType.PRE) {
                if (wallHack) {
                    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                    GL11.glPolygonOffset(1.0F, -2000000F);
                }
                if (outline) {
                    final Color clr = getEntityColor(event.getEntity());
                    GlStateManager.depthMask(true);
                    event.getRenderer().doRender(event.getEntity(), event.getX(), event.getY(), event.getZ(), event.getEntityYaw(), event.getPartialTicks());
                    OutlineUtil.renderOne();
                    event.getRenderer().doRender(event.getEntity(), event.getX(), event.getY(), event.getZ(), event.getEntityYaw(), event.getPartialTicks());
                    OutlineUtil.renderTwo();
                    event.getRenderer().doRender(event.getEntity(), event.getX(), event.getY(), event.getZ(), event.getEntityYaw(), event.getPartialTicks());
                    OutlineUtil.renderThree();
                    OutlineUtil.renderFour(clr);
                    event.getRenderer().doRender(event.getEntity(), event.getX(), event.getY(), event.getZ(), event.getEntityYaw(), event.getPartialTicks());
                    OutlineUtil.renderFive();
                    event.setCancelled(true);
                }
            } else {
                if (wallHack) {
                    GL11.glPolygonOffset(1.0F, 2000000F);
                    GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                }
            }
        }
    }

    @Subscribe
    public void onRenderHand(Render3DEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (skeleton) {
            startEnd(true);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glDisable(2848);
            entities.keySet().removeIf(this::doesntContain);
            mc.world.playerEntities.forEach(player -> drawSkeleton(event, player));
            Gui.drawRect(0, 0, 0, 0, 0);
            startEnd(false);
        }
        if (chests || brewingStands || furnaces || enchanttable || shuklers || redstoneBlocks || spawners) {
            mc.world.loadedTileEntityList.forEach(tile -> {
                final double posX = tile.getPos().getX() - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
                final double posY = tile.getPos().getY() - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
                final double posZ = tile.getPos().getZ() - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
                if (tile instanceof TileEntityChest) {
                    if (chests) {
                        AxisAlignedBB bb = new AxisAlignedBB(0.0625, 0.0, 0.0625, 1, 0.875, 1).offset(posX, posY, posZ).contract(0.0625, 0, 0.0625);
                        TileEntityChest adjacent = null;
                        if (((TileEntityChest) tile).adjacentChestXNeg != null)
                            adjacent = ((TileEntityChest) tile).adjacentChestXNeg;
                        if (((TileEntityChest) tile).adjacentChestXPos != null)
                            adjacent = ((TileEntityChest) tile).adjacentChestXPos;
                        if (((TileEntityChest) tile).adjacentChestZNeg != null)
                            adjacent = ((TileEntityChest) tile).adjacentChestZNeg;
                        if (((TileEntityChest) tile).adjacentChestZPos != null)
                            adjacent = ((TileEntityChest) tile).adjacentChestZPos;
                        if (adjacent != null) {
                            bb = bb.union(new AxisAlignedBB(0.0625, 0.0, 0.0625, 1, 0.875, 1).offset(adjacent.getPos().getX() - ((IRenderManager) mc.getRenderManager()).getRenderPosX(), adjacent.getPos().getY() - ((IRenderManager) mc.getRenderManager()).getRenderPosY(), adjacent.getPos().getZ() - ((IRenderManager) mc.getRenderManager()).getRenderPosZ())).contract(0.0625, 0, 0.0625);
                        }
                        if (((TileEntityChest) tile).getChestType() == BlockChest.Type.TRAP) {
                            RenderUtil.drawESP(bb, 255f, 91f, 86f, 40F);
                            RenderUtil.drawESPOutline(bb, 255f, 91f, 86f, 255f, 1f);
                        } else {
                            RenderUtil.drawESP(bb, 255f, 227f, 0f, 40F);
                            RenderUtil.drawESPOutline(bb, 255f, 227f, 0f, 255f, 1f);
                        }
                    }
                }
                if (tile instanceof TileEntityEnderChest) {
                    if (chests) {
                        RenderUtil.drawESP(new AxisAlignedBB(0.0625, 0.0, 0.0625, 1, 0.875, 1).offset(posX, posY, posZ).contract(0.0625, 0, 0.0625), 78f, 197f, 255f, 40F);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0625, 0.0, 0.0625, 1, 0.875, 1).offset(posX, posY, posZ).contract(0.0625, 0, 0.0625), 78f, 197f, 255f, 255f, 1f);
                    }
                }
                if (tile instanceof TileEntityBrewingStand) {
                    if (brewingStands) {
                        RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 234f, 255f, 96f, 40F);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 234f, 255f, 96f, 255f, 1f);
                    }
                }
                if (tile instanceof TileEntityFurnace) {
                    if (furnaces) {
                        RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 254f, 124f, 0f, 40f);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 254f, 124f, 0f, 255f, 1f);
                    }
                }
                if (tile instanceof TileEntityEnchantmentTable) {
                    if (enchanttable) {
                        RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 255F, 90, 12F, 40F);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 255F, 90, 12F, 255f, 1f);
                    }
                }
                if (tile instanceof TileEntityShulkerBox) {
                    if (shuklers) {
                        final Color shulkercolor = new Color(((TileEntityShulkerBox) tile).getColor().getColorValue());
                        RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), shulkercolor.getRed(), shulkercolor.getGreen(), shulkercolor.getBlue(), 40F);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), shulkercolor.getRed(), shulkercolor.getGreen(), shulkercolor.getBlue(), 255f, 1f);
                    }
                }
                if (tile instanceof TileEntityHopper || tile instanceof TileEntityDispenser) {
                    if (redstoneBlocks) {
                        RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 161f, 161f, 161f, 40F);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 161f, 161f, 161f, 255f, 1f);
                    }
                }
                if (tile instanceof TileEntityMobSpawner) {
                    if (spawners) {
                        final Color rainbow = new Color(RenderUtil.getRainbow(3000, 0, 0.85f));
                        RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), 40F);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), 255f, 1f);
                    }
                }
            });
        }
        float partialTicks = event.getPartialTicks();
        if ((boundingbox && (boxmode.equalsIgnoreCase("3D") || boxmode.equalsIgnoreCase("BOTH"))) || tracers) {
            mc.world.loadedEntityList.forEach(entity -> {
                if (entity instanceof EntityLivingBase) {
                    final EntityLivingBase ent = (EntityLivingBase) entity;
                    if (isValid(ent)) {
                        final Color clr = getEntityColor(entity);
                        final double posX = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, partialTicks) - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
                        final double posY = RenderUtil.interpolate(entity.posY, entity.lastTickPosY, partialTicks) - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
                        final double posZ = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks) - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
                        if (boundingbox && (boxmode.equalsIgnoreCase("3D") || boxmode.equalsIgnoreCase("BOTH"))) {
                            RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, entity.width, entity.height, entity.width).offset(posX - entity.width / 2, posY, posZ - entity.width / 2), clr.getRed(), clr.getGreen(), clr.getBlue(), 40F);
                            RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, entity.width, entity.height, entity.width).offset(posX - entity.width / 2, posY, posZ - entity.width / 2), clr.getRed(), clr.getGreen(), clr.getBlue(), 255f, 1f);
                        }
                        if (tracers) {
                            GL11.glPushMatrix();
                            GL11.glLoadIdentity();
                            GL11.glDisable(2929);
                            GL11.glDisable(3553);
                            GL11.glEnable(3042);
                            GL11.glBlendFunc(770, 771);
                            GL11.glEnable(GL11.GL_LINE_SMOOTH);
                            GL11.glLineWidth(1);
                            GL11.glLoadIdentity();
                            ((IEntityRenderer) mc.entityRenderer).cameraOrientation(mc.getRenderPartialTicks());
                            Vec3d eyes = new Vec3d(0, 0, 1)
                                    .rotatePitch(-(float) Math.toRadians(mc.player.rotationPitch))
                                    .rotateYaw(-(float) Math.toRadians(mc.player.rotationYaw));
                            GL11.glColor3f(clr.getRed() / 255f, clr.getGreen() / 255f, clr.getBlue() / 255f);
                            GlStateManager.glBegin(GL11.GL_LINE_STRIP);
                            {
                                GlStateManager.glVertex3f(((float) eyes.x), mc.player.getEyeHeight() + ((float) eyes.y), ((float) eyes.z));
                                GlStateManager.glVertex3f((float) posX, (float) posY, (float) posZ);
                            }
                            GlStateManager.glEnd();
                            GL11.glDisable(GL11.GL_LINE_SMOOTH);
                            GL11.glDisable(3042);
                            GL11.glEnable(3553);
                            GL11.glEnable(2929);
                            GL11.glPopMatrix();
                            GL11.glColor4f(1f, 1f, 1f, 1f);
                        }
                    }
                }
            });
        }
    }

    private void drawArmor(EntityPlayer player, int x, int y) {
        if (!player.inventory.armorInventory.isEmpty()) {
            List<ItemStack> items = new ArrayList<>();
            if (player.getHeldItem(EnumHand.OFF_HAND) != ItemStack.EMPTY) {
                items.add(player.getHeldItem(EnumHand.OFF_HAND));
            }
            if (player.getHeldItem(EnumHand.MAIN_HAND) != ItemStack.EMPTY) {
                items.add(player.getHeldItem(EnumHand.MAIN_HAND));
            }
            for (int index = 3; index >= 0; index--) {
                ItemStack stack = player.inventory.armorInventory.get(index);
                if (stack != ItemStack.EMPTY) {
                    items.add(stack);
                }
            }
            int armorX = x - ((items.size() * 19) / 2);
            for (ItemStack stack : items) {
                GlStateManager.pushMatrix();
                GlStateManager.enableLighting();
                mc.getRenderItem().renderItemIntoGUI(stack, armorX, y);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, stack, armorX, y, "");
                GlStateManager.disableLighting();
                GlStateManager.popMatrix();
                GlStateManager.disableDepth();
                if (stack.isStackable() && stack.getCount() > 0) {
                    mc.fontRenderer.drawStringWithShadow(String.valueOf(stack.getCount()), armorX + 4, y + 8, 0xDDD1E6);
                }
                NBTTagList enchants = stack.getEnchantmentTagList();
                GlStateManager.pushMatrix();
                if (stack.getItem() == Items.GOLDEN_APPLE && stack.getMetadata() == 1) {
                    mc.fontRenderer.drawStringWithShadow("op", armorX, y, 0xFFFF0000);
                }
                int ency = y + 4;
                if (!enchants.hasNoTags()) {
                    for (NBTBase nbtBase : enchants) {
                        if (nbtBase.getId() == 10) {
                            NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtBase;
                            short id = nbtTagCompound.getShort("id");
                            short level = nbtTagCompound.getShort("lvl");
                            Enchantment enc = Enchantment.getEnchantmentByID(id);

                            if (enc != null) {
                                String encName = cachedEnchantmentMap.get(enc.getName()) + level;
                                mc.fontRenderer.drawStringWithShadow(encName, armorX + 4, ency, enc.isCurse() ? 0xff9999 : 0xDDD1E6);
                                ency -= 8;
                            }
                        }
                    }
                }
                if (stack.isItemStackDamageable()) {
                    final float green = ((float) stack.getMaxDamage() - (float) stack.getItemDamage()) / (float) stack.getMaxDamage();
                    final float red = 1.0f - green;
                    final int dmg = 100 - (int) (red * 100.0f);
                    mc.fontRenderer.drawStringWithShadow(dmg + "", armorX + 4, ency, new Color(MathUtil.clamp((int) (red * 255.0f), 0, 255), MathUtil.clamp((int) (green * 255.0f), 0, 255), 0).getRGB());
                }
                GlStateManager.enableDepth();
                GlStateManager.popMatrix();
                armorX += 19;
            }
        }
    }

    private void drawSkeleton(Render3DEvent event, EntityPlayer e) {
        final Color clr = getEntityColor(e);
        float[][] entPos = entities.get(e);
        if (entPos != null && e.getEntityId() != -1488 && e.isEntityAlive() && RenderUtil.isInViewFrustrum(e) && !e.isDead && e != mc.player && !e.isPlayerSleeping()) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glLineWidth(1);
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            Vec3d vec = getVec3(event, e);
            double x = vec.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
            double y = vec.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
            double z = vec.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
            GL11.glTranslated(x, y, z);
            float xOff = e.prevRenderYawOffset + (e.renderYawOffset - e.prevRenderYawOffset) * event.getPartialTicks();
            GL11.glRotatef(-xOff, 0.0F, 1.0F, 0.0F);
            GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? -0.235D : 0.0D);
            float yOff = e.isSneaking() ? 0.6F : 0.75F;
            GL11.glPushMatrix();
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            GL11.glTranslated(-0.125D, yOff, 0.0D);
            if (entPos[3][0] != 0.0F) {
                GL11.glRotatef(entPos[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            if (entPos[3][1] != 0.0F) {
                GL11.glRotatef(entPos[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if (entPos[3][2] != 0.0F) {
                GL11.glRotatef(entPos[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, (-yOff), 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            GL11.glTranslated(0.125D, yOff, 0.0D);
            if (entPos[4][0] != 0.0F) {
                GL11.glRotatef(entPos[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            if (entPos[4][1] != 0.0F) {
                GL11.glRotatef(entPos[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if (entPos[4][2] != 0.0F) {
                GL11.glRotatef(entPos[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, (-yOff), 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? 0.25D : 0.0D);
            GL11.glPushMatrix();
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            GL11.glTranslated(0.0D, e.isSneaking() ? -0.05D : 0.0D, e.isSneaking() ? -0.01725D : 0.0D);
            GL11.glPushMatrix();
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            GL11.glTranslated(-0.375D, yOff + 0.55D, 0.0D);
            if (entPos[1][0] != 0.0F) {
                GL11.glRotatef(entPos[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            if (entPos[1][1] != 0.0F) {
                GL11.glRotatef(entPos[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if (entPos[1][2] != 0.0F) {
                GL11.glRotatef(-entPos[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, -0.5D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(0.375D, yOff + 0.55D, 0.0D);
            if (entPos[2][0] != 0.0F) {
                GL11.glRotatef(entPos[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            if (entPos[2][1] != 0.0F) {
                GL11.glRotatef(entPos[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if (entPos[2][2] != 0.0F) {
                GL11.glRotatef(-entPos[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, -0.5D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glRotatef(xOff - e.rotationYawHead, 0.0F, 1.0F, 0.0F);
            GL11.glPushMatrix();
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
            if (entPos[0][0] != 0.0F) {
                GL11.glRotatef(entPos[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            }
            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, 0.3D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glRotatef(e.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslated(0.0D, e.isSneaking() ? -0.16175D : 0.0D, e.isSneaking() ? -0.48025D : 0.0D);
            GL11.glPushMatrix();
            GL11.glTranslated(0.0D, yOff, 0.0D);
            GL11.glBegin(3);
            GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
            GL11.glVertex3d(0.125D, 0.0D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            GL11.glTranslated(0.0D, yOff, 0.0D);
            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, 0.55D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
            GL11.glBegin(3);
            GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
            GL11.glVertex3d(0.375D, 0.0D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
    }

    private Vec3d getVec3(Render3DEvent event, EntityPlayer var0) {
        float pt = event.getPartialTicks();
        double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * pt;
        double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * pt;
        double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * pt;
        return new Vec3d(x, y, z);
    }

    public void addEntity(EntityPlayer e, ModelPlayer model) {
        entities.put(e, new float[][]{{model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ}, {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ}, {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ}, {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ}, {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}});
    }

    private boolean doesntContain(EntityPlayer var0) {
        return !mc.world.playerEntities.contains(var0);
    }

    private void startEnd(boolean revert) {
        if (revert) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GL11.glEnable(2848);
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GL11.glHint(3154, 4354);
        } else {
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GL11.glDisable(2848);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask(!revert);
    }

    private boolean isValid(EntityLivingBase entity) {
        return mc.player != entity && entity.getEntityId() != -1488 && isValidType(entity) && entity.isEntityAlive() && (!entity.isInvisible() || invisibles);
    }

    private boolean isValidType(EntityLivingBase entity) {
        return (players && entity instanceof EntityPlayer) || ((monsters && (entity instanceof EntityMob || entity instanceof EntitySlime)) || (passives && (entity instanceof EntityVillager || entity instanceof EntityGolem)) || (animals && entity instanceof IAnimals));
    }

    private int getHealthColor(EntityLivingBase player) {
        return Color.HSBtoRGB(Math.max(0.0F, Math.min(player.getHealth(), player.getMaxHealth()) / player.getMaxHealth()) / 3.0F, 1.0F, 0.8f) | 0xFF000000;
    }

    private Color getEntityColor(Entity entity) {
        return new Color(IngrosWare.INSTANCE.friendManager.isFriend(entity.getName()) ? 0xff2020ff : (entity.isSneaking() ? 0xffffff00 : playerColor.getRGB()));
    }
}
