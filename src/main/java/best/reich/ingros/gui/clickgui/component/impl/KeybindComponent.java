package best.reich.ingros.gui.clickgui.component.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.gui.clickgui.component.Component;
import best.reich.ingros.module.modules.render.ClickGui;
import best.reich.ingros.util.game.MouseUtil;
import best.reich.ingros.util.logging.Logger;
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.util.font.Fonts;
import org.lwjgl.input.Keyboard;

public class KeybindComponent extends Component {
    private final ToggleableModule toggleableModule;
    private boolean binding;
    public KeybindComponent(ToggleableModule toggleableModule, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(toggleableModule.getLabel(), posX, posY, offsetX, offsetY, width, height);
        this.toggleableModule = toggleableModule;
    }

    @Override
    public void init() {
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        final ClickGui clickGui = (ClickGui) IngrosWare.INSTANCE.moduleManager.getModule("ClickGui");
        RenderUtil.drawRect(getFinishedX(), getFinishedY(), getWidth(), getHeight(), 0x92000000);
        RenderUtil.drawBorderedRect(getFinishedX() + 5, getFinishedY() + 1f, getWidth() - 10, getHeight() - 2,0.5f,0x40000000, clickGui.color.getRGB());
        Fonts.arialFont.drawStringWithShadow(isBinding() ? "Press a key...":"Bind: " + Keyboard.getKeyName(getToggleableModule().getBind()), getFinishedX() + 6, getFinishedY() + 0.5f+ getHeight() / 2 - Fonts.arialFont.getStringHeight(getLabel()) / 2, 0xFFFFFFFF);
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (isBinding()) {
            getToggleableModule().setBind(keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_SPACE || keyCode == Keyboard.KEY_DELETE ? Keyboard.KEY_NONE : keyCode);
            Logger.printMessage("Bound " + getLabel() + " to " + Keyboard.getKeyName(getToggleableModule().getBind()),true);
            setBinding(false);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = MouseUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5, getFinishedY() + 1f, getWidth() - 10, getHeight() - 2);
        if (hovered && mouseButton == 0) setBinding(!isBinding());
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public ToggleableModule getToggleableModule() {
        return toggleableModule;
    }

    public boolean isBinding() {
        return binding;
    }

    public void setBinding(boolean binding) {
        this.binding = binding;
    }
}
