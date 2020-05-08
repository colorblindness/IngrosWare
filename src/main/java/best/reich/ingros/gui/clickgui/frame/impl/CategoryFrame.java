package best.reich.ingros.gui.clickgui.frame.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.gui.clickgui.component.Component;
import best.reich.ingros.gui.clickgui.component.impl.ModuleComponent;
import best.reich.ingros.gui.clickgui.frame.Frame;
<<<<<<< Updated upstream
=======
import best.reich.ingros.module.modules.render.ClickGui;
import best.reich.ingros.util.game.MouseUtil;
>>>>>>> Stashed changes
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.module.IModule;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.util.font.Fonts;

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
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderUtil.drawRect(getPosX(), getPosY(), getWidth(), getHeight(), 0xFFB3002E);
        RenderUtil.drawRect(getPosX(), getPosY() + getHeight() - 0.5f, getWidth(), 0.5f, 0xFF323232);
        Fonts.arialFont.drawStringWithShadow(getLabel(), getPosX() + 3, getPosY() + getHeight() / 2 - Fonts.arialFont.getStringHeight(getLabel()) / 2, 0xFFFFFFFF);
        if (isExtended())
            RenderUtil.drawRect(getPosX(), getPosY() + getHeight(), getWidth(), 1, 0x92000000);
        updatePositions();
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private void updatePositions() {
        float offsetY = getHeight() + 1;
        for (Component component : getComponents()) {
            component.setOffsetY(offsetY);
            component.moved(getPosX(),getPosY());
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

    public ModuleCategory getModuleCategory() {
        return moduleCategory;
    }
}
