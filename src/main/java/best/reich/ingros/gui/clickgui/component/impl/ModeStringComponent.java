package best.reich.ingros.gui.clickgui.component.impl;

import best.reich.ingros.gui.clickgui.component.Component;
import best.reich.ingros.util.game.MouseUtil;
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.setting.impl.ModeStringSetting;
import me.xenforu.kelo.util.font.Fonts;

public class ModeStringComponent extends Component {
    private final ModeStringSetting modeStringSetting;

    public ModeStringComponent(ModeStringSetting modeStringSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(modeStringSetting.getLabel(), posX, posY, offsetX, offsetY, width, height);
        this.modeStringSetting = modeStringSetting;
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
        RenderUtil.drawRect(getFinishedX(), getFinishedY(), getWidth(), getHeight(), 0x92000000);
        Fonts.arialFont.drawStringWithShadow(getLabel() + ": " + getModeStringSetting().getValue(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - Fonts.arialFont.getStringHeight(getLabel()) / 2, 0xFFFFFFFF);
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = MouseUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());
        if (hovered) {
            if (mouseButton == 0) getModeStringSetting().increment();
            else if (mouseButton == 1) getModeStringSetting().decrement();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public ModeStringSetting getModeStringSetting() {
        return modeStringSetting;
    }
}
