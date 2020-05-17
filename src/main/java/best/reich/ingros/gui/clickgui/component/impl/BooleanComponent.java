package best.reich.ingros.gui.clickgui.component.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.gui.clickgui.component.Component;
import best.reich.ingros.module.modules.render.ClickGui;
import best.reich.ingros.util.game.MouseUtil;
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.setting.impl.BooleanSetting;
import me.xenforu.kelo.util.font.Fonts;

public class BooleanComponent extends Component {
    private final BooleanSetting booleanSetting;

    public BooleanComponent(BooleanSetting booleanSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(booleanSetting.getLabel(), posX, posY, offsetX, offsetY, width, height);
        this.booleanSetting = booleanSetting;
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
        RenderUtil.drawBorderedRect(getFinishedX() + getWidth() - 17,getFinishedY() + 1,12,getHeight() - 2,0.5f,getBooleanSetting().getValue() ? clickGui.color.brighter().getRGB():0x40000000,clickGui.color.getRGB());
        if (getBooleanSetting().getValue())
            RenderUtil.drawCheckMark(getFinishedX() + getWidth() - 11,getFinishedY() + 1,10,0xFFFFFFFF);
        Fonts.arialFont.drawStringWithShadow(getLabel(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - Fonts.arialFont.getStringHeight(getLabel()) / 2, getBooleanSetting().getValue() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = MouseUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + getWidth() - 14,getFinishedY() + 1,12,getHeight() - 2);
        if (hovered && mouseButton == 0) getBooleanSetting().setValue(!getBooleanSetting().getValue());
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public BooleanSetting getBooleanSetting() {
        return booleanSetting;
    }
}
