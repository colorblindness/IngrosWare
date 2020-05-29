package best.reich.ingros.module.persistent;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.events.render.Render2DEvent;
import best.reich.ingros.module.modules.combat.KillAura;
import best.reich.ingros.module.modules.other.TotemPopCounter;
import best.reich.ingros.util.game.TickRate;
import best.reich.ingros.util.render.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.PersistentModule;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Mode;
import me.xenforu.kelo.setting.annotation.Setting;
import me.xenforu.kelo.util.math.MathUtil;
import me.xenforu.kelo.util.math.TimerUtil;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;

@ModuleManifest(label = "Overlay", category = ModuleCategory.RENDER)
public class Overlay extends PersistentModule {
    @Setting("ColorMode")
    @Mode({"NORMAL", "CLIENT", "RAINBOW"})
    public String colormode = "NORMAL";

    @Setting("Font")
    public boolean font = true;

    @Setting("ArrayList")
    public boolean arraylist = true;

    @Setting("ShowSuffix")
    public boolean showsuffix = true;

    @Setting("Armor")
    public boolean armor = true;

    @Setting("XYZ")
    public boolean xyz = true;

    @Setting("FPS")
    public boolean fps = true;

    @Setting("TPS")
    public boolean tps = true;

    @Setting("Totems")
    public boolean totems = true;

    @Setting("Inventory")
    public boolean inventory = true;

    @Setting("Ping")
    public boolean ping = true;

    @Setting("Greetings")
    public boolean greetings = true;

    @Setting("Potions")
    public boolean potions = true;

    @Setting("NotResponding")
    public boolean notresponding = true;

    @Setting("Notifications")
    public boolean notifications = true;

    @Setting("TargetHUD")
    public boolean targetHUD = true;

    @Setting("ClientColor")
    public Color clientColor = new Color(234, 38, 38);

    private final TimerUtil potionTimer = new TimerUtil();
    private final TimerUtil serverTimer = new TimerUtil();
    private int initialRenderPos = 2;
    private final ResourceLocation INVENTORY_RESOURCE = new ResourceLocation("textures/gui/container/inventory.png");
    private Gui gui = new Gui();

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getType() == EventType.POST) {
            TickRate.update(event);
            serverTimer.reset();
        }
    }

    @Subscribe
    public void onRender(Render2DEvent event) {
        if (mc.world == null || mc.player == null || mc.gameSettings.showDebugInfo) return;
        RenderUtil.drawText(IngrosWare.INSTANCE.getLabel() + ChatFormatting.WHITE + " (" + IngrosWare.INSTANCE.getVersion() + ")", 2, 2, getHudColor(), font);
        if (arraylist) {
            int togglesY = (int) (initialRenderPos - RenderUtil.getTextHeight(font) - 2);
            ArrayList<ToggleableModule> modules = new ArrayList<>(IngrosWare.INSTANCE.moduleManager.getToggles());
            modules.sort(Comparator.comparingDouble(m -> -RenderUtil.getTextWidth(getRenderLabel(m), font)));
            for (ToggleableModule module : modules) {
                if (!module.isEnabled() || module.isHidden()) continue;
                RenderUtil.drawText(getRenderLabel(module), event.getScaledResolution().getScaledWidth() - RenderUtil.getTextWidth(getRenderLabel(module), font) - 2, togglesY += RenderUtil.getTextHeight(font) + 2, getArrayListColor(module, togglesY), font);
            }
            if (potionTimer.reach(10)) {
                for (int i = 0; i < (Minecraft.getDebugFPS() > 45 ? 4 : 8); i++) {
                    Collection<PotionEffect> collection = mc.player.getActivePotionEffects();
                    Potion potion = null;
                    for (PotionEffect potioneffect : collection) {
                        if (potioneffect.getPotion().hasStatusIcon()) potion = potioneffect.getPotion();
                    }
                    if (collection.size() > 0 && potion != null && potion.hasStatusIcon()) {
                        if (initialRenderPos < 54 && (!potion.isBeneficial() || potion.isBadEffect())) {
                            initialRenderPos++;
                        }
                        if (potion.isBeneficial()) {
                            if (initialRenderPos < 28) {
                                initialRenderPos++;
                            }
                            if (initialRenderPos > 28) {
                                initialRenderPos--;
                            }
                        }
                    } else if (initialRenderPos > 3) {
                        initialRenderPos--;
                        potionTimer.reset();
                    }
                }
            }
        }
        if (getTarget() != null && getTarget() instanceof EntityPlayer && targetHUD) {
                Gui.drawRect(event.getScaledResolution().getScaledWidth() / 2 - 14, event.getScaledResolution().getScaledHeight() / 2 + 25 + 50, event.getScaledResolution().getScaledWidth() / 2 + 14, event.getScaledResolution().getScaledHeight() / 2 - 1 + 50, new Color(0, 0, 0, 255).getRGB());
                Gui.drawRect(event.getScaledResolution().getScaledWidth() / 2 - 52, event.getScaledResolution().getScaledHeight() / 2 + 23 + 50, event.getScaledResolution().getScaledWidth() / 2 + 52, event.getScaledResolution().getScaledHeight() / 2 + 52 + 50, new Color(0, 0, 0, 255).getRGB());
                Gui.drawRect(event.getScaledResolution().getScaledWidth() / 2 - 51, event.getScaledResolution().getScaledHeight() / 2 + 24 + 50, event.getScaledResolution().getScaledWidth() / 2 + 51, event.getScaledResolution().getScaledHeight() / 2 + 51 + 50, new Color(45, 45, 45, 255).getRGB());
                Gui.drawRect(event.getScaledResolution().getScaledWidth() / 2 - 50, event.getScaledResolution().getScaledHeight() / 2 + 25 + 50, event.getScaledResolution().getScaledWidth() / 2 + 50, event.getScaledResolution().getScaledHeight() / 2 + 50 + 50, new Color(15, 15, 15, 255).getRGB());
                Gui.drawRect(event.getScaledResolution().getScaledWidth() / 2 - 13, event.getScaledResolution().getScaledHeight() / 2 + 24 + 50, event.getScaledResolution().getScaledWidth() / 2 + 13, event.getScaledResolution().getScaledHeight() / 2 + 50, new Color(45, 45, 45, 255).getRGB());
                drawAltFace(getTarget(), event.getScaledResolution().getScaledWidth() / 2 - 12, event.getScaledResolution().getScaledHeight() / 2 + 1 + 50, 24, 24);
                RenderUtil.drawText(getTarget().getName(), event.getScaledResolution().getScaledWidth() / 2 - RenderUtil.getTextWidth(getTarget().getName(), font) / 2, event.getScaledResolution().getScaledHeight() / 2 + 27 + 50, -1, font);
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.5, 0.5, 0.5);

                if (IngrosWare.INSTANCE.moduleManager.getToggleByName("TotemPopCounter").isEnabled() && TotemPopCounter.popList.containsKey(getTarget().getName()))
                    RenderUtil.drawText("Pops:" + TotemPopCounter.popList.get(getTarget().getName()), event.getScaledResolution().getScaledWidth() - 150 / 2, event.getScaledResolution().getScaledHeight() + 75 + 100, -1, font);

                RenderUtil.drawText("HP: " + Math.floor(getTarget().getHealth()), event.getScaledResolution().getScaledWidth() - RenderUtil.getTextWidth("HP: " + Math.floor(getTarget().getHealth()), font) / 2, event.getScaledResolution().getScaledHeight() + 75 + 100, -1, font);

                GlStateManager.scale(1, 1, 1);
                GlStateManager.popMatrix();
                RenderUtil.drawRect((event.getScaledResolution().getScaledWidth() / 2) - 48, (event.getScaledResolution().getScaledHeight() / 2) + 45 + 50, (((getTarget().getHealth() > 20 ? 20 : getTarget().getHealth()) / 2) * 9.60f), 3, new Color(45, 45, 45, 255).getRGB());
                RenderUtil.drawRect((event.getScaledResolution().getScaledWidth() / 2) - 47, (event.getScaledResolution().getScaledHeight() / 2) + 46 + 50,(((getTarget().getHealth() > 20 ? 20 : getTarget().getHealth()) / 2) * 9.40f), 1, getHealthColor(getTarget()));
        }
        if (armor) drawArmor(event.getScaledResolution());
        if (xyz) {
            final long x = Math.round(mc.player.posX);
            final long y = Math.round(mc.player.posY);
            final long z = Math.round(mc.player.posZ);
            final String coords = mc.player.dimension == -1 ? String.format(ChatFormatting.PREFIX_CODE + "7%s " + ChatFormatting.PREFIX_CODE + "f(%s)" + ChatFormatting.PREFIX_CODE + "8, " + ChatFormatting.PREFIX_CODE + "7%s " + ChatFormatting.PREFIX_CODE + "f(%s)" + ChatFormatting.PREFIX_CODE + "8, " + ChatFormatting.PREFIX_CODE + "7%s " + ChatFormatting.PREFIX_CODE + "f(%s)", x, x * 8, y, y * 8, z, z * 8) : String.format(ChatFormatting.PREFIX_CODE + "f%s " + ChatFormatting.PREFIX_CODE + "7(%s)" + ChatFormatting.PREFIX_CODE + "8, " + ChatFormatting.PREFIX_CODE + "f%s " + ChatFormatting.PREFIX_CODE + "7(%s)" + ChatFormatting.PREFIX_CODE + "8, " + ChatFormatting.PREFIX_CODE + "f%s " + ChatFormatting.PREFIX_CODE + "7(%s)", x, x / 8, y, y, z, z / 8);
            RenderUtil.drawText(coords, 2, event.getScaledResolution().getScaledHeight() - (mc.ingameGUI.getChatGUI().getChatOpen() ? RenderUtil.getTextHeight(font) + 14 : RenderUtil.getTextHeight(font) + 2), getHudColor(), font);
        }
        if (fps)
            RenderUtil.drawText("FPS: " + ChatFormatting.WHITE + Minecraft.getDebugFPS(), 2, event.getScaledResolution().getScaledHeight() - (mc.ingameGUI.getChatGUI().getChatOpen() ? RenderUtil.getTextHeight(font) + 14 : RenderUtil.getTextHeight(font) + 2) - (xyz ? RenderUtil.getTextHeight(font) + 2 : 0), getHudColor(), font);
        if (tps)
            RenderUtil.drawText((fps ? ", TPS: " : "TPS: ") + ChatFormatting.WHITE + TickRate.TPS, 2 + (fps ? RenderUtil.getTextWidth("FPS: " + ChatFormatting.WHITE + Minecraft.getDebugFPS(), font) : 0), event.getScaledResolution().getScaledHeight() - (mc.ingameGUI.getChatGUI().getChatOpen() ? RenderUtil.getTextHeight(font) + 14 : RenderUtil.getTextHeight(font) + 2) - (xyz ? RenderUtil.getTextHeight(font) + 2 : 0), getHudColor(), font);
        float y = 4 + RenderUtil.getTextHeight(font);
        if (totems) {
            RenderUtil.drawText("Totems: " + ChatFormatting.WHITE + totemCount(), 2, y, getHudColor(), font);
            y += RenderUtil.getTextHeight(font) + 2;
        }
        if (ping) {
            final NetworkPlayerInfo networkPlayerInfo = mc.getConnection().getPlayerInfo(mc.player.getGameProfile().getId());
            final String ping = networkPlayerInfo == null ? "0ms" : networkPlayerInfo.getResponseTime() + " ms";
            RenderUtil.drawText("Ping: " + ChatFormatting.WHITE + ping, 2, y, getHudColor(), font);
            y += RenderUtil.getTextHeight(font) + 2;
        }
        if (inventory) {
            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            RenderUtil.drawBorderedRect(event.getScaledResolution().getScaledWidth() / 2 + 95, event.getScaledResolution().getScaledHeight() - 50, 144, 48, 1, 0x75101010, 0x90000000);
            for (int i = 0; i < 27; i++) {
                final ItemStack itemStack = mc.player.inventory.mainInventory.get(i + 9);
                int offsetX = ((event.getScaledResolution().getScaledWidth() / 2 + 95) + (i % 9) * 16);
                int offsetY = ((event.getScaledResolution().getScaledHeight() - 50) + (i / 9) * 16);
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX, offsetY);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, offsetX, offsetY, null);
            }
            RenderHelper.disableStandardItemLighting();
            mc.getRenderItem().zLevel = 0.0F;
            GlStateManager.popMatrix();
        }
        if (greetings)
            RenderUtil.drawText(getGreetings() + mc.player.getName() + ".", event.getScaledResolution().getScaledWidth() / 2 - RenderUtil.getTextWidth(getGreetings() + mc.player.getName() + ".", font) / 2, 2, getHudColor(), font);
        if (notresponding && serverTimer.reach(1000))
            RenderUtil.drawText("Server has not responded for \247r" + new DecimalFormat("0.0").format((double) serverTimer.time() / 1000) + "s", event.getScaledResolution().getScaledWidth() / 2 - RenderUtil.getTextWidth("Server has not responded for " + new DecimalFormat("0.0").format(serverTimer.time() / 1000) + "s", font) / 2, 2 + (greetings ? RenderUtil.getTextHeight(font) + 2 : 0), getHudColor(), font);
        if (potions) drawPotions(event.getScaledResolution());
        if (notifications) IngrosWare.INSTANCE.notificationManager.renderNotifications();
    }

    private void drawArmor(ScaledResolution scaledResolution) {
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        int i = scaledResolution.getScaledWidth() / 2;
        int iteration = 0;
        int y = scaledResolution.getScaledHeight() - 55 - (mc.player.isInWater() ? 10 : 0);
        for (ItemStack is : mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            mc.getRenderItem().zLevel = 200.0f;
            mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, y);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
            mc.getRenderItem().zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            RenderUtil.drawText(s, (float) (x + 19 - 2 - RenderUtil.getTextWidth(s, font)), (float) (y + 9), 16777215, font);
            final float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
            final float red = 1.0f - green;
            final int dmg = 100 - (int) (red * 100.0f);
            RenderUtil.drawText(dmg + "", (float) (x + 8 - RenderUtil.getTextWidth(dmg + "", font) / 2), (float) (y - 9), new Color(MathUtil.clamp((int) (red * 255.0f), 0, 255), MathUtil.clamp((int) (green * 255.0f), 0, 255), 0).getRGB(), font);
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
    }

    private void drawPotions(ScaledResolution sr) {
        final ArrayList<Potion> sorted = new ArrayList<>();
        int height = -(mc.ingameGUI.getChatGUI().getChatOpen() ? 12 : 0);
        for (final Potion potion : Potion.REGISTRY) {
            if (potion != null) {
                if (mc.player.isPotionActive(potion)) {
                    if (potion.hasStatusIcon()) {
                        sorted.add(potion);
                    }
                }
            }
        }
        sorted.sort(Comparator.comparingDouble(potion -> -RenderUtil.getTextWidth(I18n.format(potion.getName()) + this.getAmplifierNumerals(Objects.requireNonNull(mc.player.getActivePotionEffect(potion)).getAmplifier()) + " : " + Potion.getPotionDurationString(Objects.requireNonNull(mc.player.getActivePotionEffect(potion)), 1.0F), font)));
        for (final Potion potion : sorted) {
            final PotionEffect effect = mc.player.getActivePotionEffect(potion);
            if (effect != null) {
                final String label = I18n.format(potion.getName()) + this.getAmplifierNumerals(effect.getAmplifier()) + ChatFormatting.GRAY + " : " + Potion.getPotionDurationString(effect, 1.0F);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                mc.getTextureManager().bindTexture(INVENTORY_RESOURCE);
                final int index = potion.getStatusIconIndex();
                final double x = sr.getScaledWidth() - 22 - RenderUtil.getTextWidth(label, font);
                gui.drawTexturedModalRect((int) x, sr.getScaledHeight() - 20 + height, index % 8 * 18, 198 + index / 8 * 18, 18, 18);
                RenderUtil.drawText(label, (float) (x + 20), sr.getScaledHeight() - 12 + height, potion.getLiquidColor(), font);
                height -= 20;
            }
        }
    }

    private String getAmplifierNumerals(int amplifier) {
        switch (amplifier) {
            case 0:
                return " I";
            case 1:
                return " II";
            case 2:
                return " III";
            case 3:
                return " IV";
            case 4:
                return " V";
            case 5:
                return " VI";
            case 6:
                return " VII";
            case 7:
                return " VIII";
            case 8:
                return " IX";
            case 9:
                return " X";
            default:
                if (amplifier < 1) {
                    return "";
                }
                return " " + amplifier + 1;
        }
    }

    private String getRenderLabel(ToggleableModule module) {
        final StringBuilder sb = new StringBuilder(module.getLabel());
        if (module.getSuffix() != null && showsuffix)
            sb.append(ChatFormatting.GRAY).append(" [").append(ChatFormatting.WHITE).append(module.getSuffix()).append(ChatFormatting.GRAY).append("]");
        return sb.toString();
    }

    private int getArrayListColor(ToggleableModule toggleableModule, int offset) {
        switch (colormode.toUpperCase()) {
            case "NORMAL":
                return toggleableModule.getColor();
            case "CLIENT":
                return clientColor.getRGB();
            case "RAINBOW":
                return RenderUtil.getRainbow(3000, 10 * offset, 0.75f);
        }
        return -1;
    }

    public int getHudColor() {
        switch (colormode.toUpperCase()) {
            case "NORMAL":
            case "CLIENT":
                return clientColor.getRGB();
            case "RAINBOW":
                return RenderUtil.getRainbow(3000, 10, 0.75f);
        }
        return -1;
    }

    private int getHealthColor(EntityLivingBase player) {
        return Color.HSBtoRGB(Math.max(0.0F, Math.min(player.getHealth(), player.getMaxHealth()) / player.getMaxHealth()) / 3.0F, 1.0F, 0.8f) | 0xFF000000;
    }

    private String getGreetings() {
        final int timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (timeOfDay < 12) {
            return "Good morning ";
        } else if (timeOfDay < 16) {
            return "Good afternoon ";
        } else if (timeOfDay < 21) {
            return "Good evening ";
        } else {
            return "Good night ";
        }
    }

    private int totemCount() {
        int count = 0;
        for (int i = 0; i < 45; ++i) {
            if (!mc.player.inventory.getStackInSlot(i).isEmpty() && mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                count++;
            }
        }
        return count;
    }

    private EntityLivingBase getTarget() {
        EntityLivingBase ent = null;
        KillAura aura = (KillAura) IngrosWare.INSTANCE.moduleManager.getToggleByName("KillAura");
        if (aura.isEnabled() && aura.target != null && aura.target instanceof EntityPlayer) {
            ent = aura.target;
        }
        return ent;
    }

    private void drawAltFace(EntityLivingBase target, int x, int y, int w, int h) {
        try {
            ResourceLocation skin = ((AbstractClientPlayer) target).getLocationSkin();
            mc.getTextureManager().bindTexture(skin);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
            float fw = 192;
            float fh = 192;
            float u = 24;
            float v = 24;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            GL11.glDisable(GL11.GL_BLEND);
        } catch (Exception e) {
        }
    }
}
