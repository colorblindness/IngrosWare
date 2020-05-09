package best.reich.ingros.gui.clickgui.frame.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.gui.clickgui.component.Component;
import best.reich.ingros.gui.clickgui.component.impl.ModuleComponent;
import best.reich.ingros.gui.clickgui.frame.Frame;
import best.reich.ingros.module.modules.render.ClickGui;
import best.reich.ingros.util.game.MouseUtil;
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.module.IModule;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.util.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class CategoryFrame extends Frame {
    private final ModuleCategory moduleCategory;
    public CategoryFrame(ModuleCategory moduleCategory, float posX, float posY, float width, float height) {
        super(moduleCategory.getLabel(), posX, posY, width, height);
        this.moduleCategory = moduleCategory;
    }

    @Override
    public void init() {
        float offsetY = getHeight() + 1;
        for (IModule module : IngrosWare.INSTANCE.moduleManager.getModulesFromCategory(getModuleCategory())) {
            getComponents().add(new ModuleComponent(module, getPosX(), getPosY(), 0, offsetY, getWidth(), 14));
            offsetY += 14;
        }
        super.init();
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX,mouseY,partialTicks);
        final ClickGui clickGui = (ClickGui) IngrosWare.INSTANCE.moduleManager.getModule("ClickGui");
        RenderUtil.drawRect(getPosX(), getPosY(), getWidth(), getHeight(), clickGui.color.getRGB());
        RenderUtil.drawRect(getPosX(), getPosY() + getHeight() - 0.5f, getWidth(), 0.5f, 0xFF323232);
        Fonts.arialFont.drawStringWithShadow(getLabel(), getPosX() + 3, getPosY() + getHeight() / 2 - Fonts.arialFont.getStringHeight(getLabel()) / 2, 0xFFFFFFFF);
        if (isExtended()) {
            if (MouseUtil.mouseWithinBounds(mouseX, mouseY, getPosX(), getPosY() + getHeight(), getWidth(),(getCurrentHeight() > 280 ? 280 : getCurrentHeight()) + 1) && getCurrentHeight() > 280) {
                int wheel = Mouse.getDWheel();
                if (wheel < 0) {
                    if (getScrollY() - 6 < -(getCurrentHeight() - Math.min(getCurrentHeight(),280))) setScrollY((int) -(getCurrentHeight() - Math.min(getCurrentHeight(),280)));
                    else setScrollY(getScrollY() - 6);
                } else if (wheel > 0) {
                    setScrollY(getScrollY() + 6);
                }
            }
            if (getScrollY() > 0) setScrollY(0);
            if (getCurrentHeight() > 280) {
                if (getScrollY() - 6 < -(getCurrentHeight() - 280))
                    setScrollY((int) -(getCurrentHeight() - 280));
            } else if (getScrollY() < 0) setScrollY(0);
            RenderUtil.drawRect(getPosX(), getPosY() + getHeight(), getWidth(), 1, 0x92000000);
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.prepareScissorBox(new ScaledResolution(Minecraft.getMinecraft()), getPosX(), getPosY() + getHeight() + 1, getWidth(),280);
            getComponents().forEach(component -> component.drawScreen(mouseX, mouseY, partialTicks));
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();
        }
        updatePositions();
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isExtended() && MouseUtil.mouseWithinBounds(mouseX, mouseY, getPosX(), getPosY() + getHeight(), getWidth(),(getCurrentHeight() > 280 ? 280 : getCurrentHeight()) + 1)) getComponents().forEach(component -> component.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private void updatePositions() {
        float offsetY = getHeight() + 1;
        for (Component component : getComponents()) {
            component.setOffsetY(offsetY);
            component.moved(getPosX(),getPosY() + getScrollY());
            if (component instanceof  ModuleComponent) {
                if (component.isExtended()) {
                    for (Component component1 : ((ModuleComponent) component).getComponents()) {
                        offsetY += component1.getHeight();
                    }
                }
            }
            offsetY += component.getHeight();
        }
    }

    private float getCurrentHeight() {
        float cHeight = 0;
        for (Component component : getComponents()) {
            if (component instanceof  ModuleComponent) {
                if (component.isExtended()) {
                    for (Component component1 : ((ModuleComponent) component).getComponents()) {
                        cHeight += component1.getHeight();
                    }
                }
            }
            cHeight += component.getHeight();
        }
        return cHeight;
    }

    private float getOriginalHeight() {
        float oHeight = 0;
        for (Component component : getComponents()) {
            oHeight += component.getHeight();
        }
        return oHeight;
    }

    public ModuleCategory getModuleCategory() {
        return moduleCategory;
    }
}
