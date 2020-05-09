package best.reich.ingros.gui.clickgui.component.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.gui.clickgui.component.Component;
import best.reich.ingros.module.modules.render.ClickGui;
import best.reich.ingros.util.game.MouseUtil;
import best.reich.ingros.util.logging.Logger;
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.module.IModule;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.AbstractSetting;
import me.xenforu.kelo.setting.impl.BooleanSetting;
import me.xenforu.kelo.setting.impl.ColorSetting;
import me.xenforu.kelo.setting.impl.ModeStringSetting;
import me.xenforu.kelo.setting.impl.NumberSetting;
import me.xenforu.kelo.util.font.Fonts;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class ModuleComponent extends Component {
    private final IModule module;
    private final ArrayList<Component> components = new ArrayList<>();
    public ModuleComponent(IModule module, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(module.getLabel(), posX, posY, offsetX, offsetY, width, height);
        this.module = module;
    }

    @Override
    public void init() {
        float offY = getHeight();
        if (IngrosWare.INSTANCE.settingManager.getSettingsFromObject(getModule()) != null) {
            for (AbstractSetting setting : IngrosWare.INSTANCE.settingManager.getSettingsFromObject(getModule())) {
                if (setting instanceof BooleanSetting) {
                    getComponents().add(new BooleanComponent((BooleanSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof NumberSetting) {
                    getComponents().add(new NumberComponent((NumberSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof ModeStringSetting) {
                    getComponents().add(new ModeStringComponent((ModeStringSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof ColorSetting) {
                    getComponents().add(new ColorComponent((ColorSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
            }
        }
        if (getModule() instanceof ToggleableModule) getComponents().add(new KeybindComponent((ToggleableModule) getModule(), getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
        getComponents().forEach(Component::init);
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
        getComponents().forEach(component -> component.moved(getFinishedX(),getFinishedY()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        final ClickGui clickGui = (ClickGui) IngrosWare.INSTANCE.moduleManager.getModule("ClickGui");
        RenderUtil.drawRect(getFinishedX(), getFinishedY(), getWidth(), getHeight(), 0x92000000);
        if (getModule().isEnabled())
            RenderUtil.drawRect(getFinishedX() + 1, getFinishedY() + 0.5f, getWidth() - 2, getHeight() - 1, clickGui.color.getRGB());
        Fonts.arialFont.drawStringWithShadow(getLabel(),getFinishedX() + 4,getFinishedY() + getHeight() / 2 - Fonts.arialFont.getStringHeight(getLabel()) / 2,getModule().isEnabled() ? 0xFFFFFFFF:0xFFAAAAAA);
        if (!getComponents().isEmpty()) Fonts.arialFont.drawStringWithShadow(isExtended() ? "-" : "+",getFinishedX() + getWidth() - 4 - Fonts.arialFont.getStringWidth(isExtended() ? "-" : "+"),getFinishedY() + getHeight() / 2 - Fonts.arialFont.getStringHeight(isExtended() ? "-" : "+") / 2,getModule().isEnabled() ? 0xFFFFFFFF:0xFFAAAAAA);
        if (isExtended()) getComponents().forEach(component -> component.drawScreen(mouseX,mouseY,partialTicks));
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (isExtended()) getComponents().forEach(component -> component.keyTyped(character,keyCode));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = MouseUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());
        if (hovered) {
            switch (mouseButton) {
                case 0:
                        if (getModule() instanceof ToggleableModule) {
                            final ToggleableModule mod = (ToggleableModule) getModule();
                            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                                mod.setHidden(!mod.isHidden());
                                Logger.printMessage("Set " + mod.getLabel() + " to " + (mod.isHidden() ? "hidden in the arraylist!" : "shown in the arraylist!"),true);
                            }else
                                mod.setEnabled(!mod.isEnabled());
                        }

                    break;
                case 1:
                    if (!getComponents().isEmpty()) setExtended(!isExtended());
                    break;
                default:
                    break;
            }
        }
        if (isExtended()) getComponents().forEach(component -> component.mouseClicked(mouseX,mouseY,mouseButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (isExtended()) getComponents().forEach(component -> component.mouseReleased(mouseX,mouseY,mouseButton));
    }

    public IModule getModule() {
        return module;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }
}
