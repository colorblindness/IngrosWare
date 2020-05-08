package best.reich.ingros.gui.clickgui.component.impl;

import best.reich.ingros.gui.clickgui.component.Component;
import best.reich.ingros.util.game.MouseUtil;
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.setting.impl.ColorSetting;
import me.xenforu.kelo.util.font.Fonts;
import org.lwjgl.input.Keyboard;

import java.awt.*;


public class ColorComponent extends Component {
    private final ColorSetting colorSetting;
    private boolean pressedhue;
    private float pos, hue, saturation, brightness;
    private boolean hovered;
    public ColorComponent(ColorSetting colorSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(colorSetting.getLabel(), posX, posY, offsetX, offsetY, width, height);
        this.colorSetting = colorSetting;
        float[] hsb = new float[3];
        final Color clr = colorSetting.getValue();
        hsb = Color.RGBtoHSB(clr.getRed(), clr.getGreen(), clr.getBlue(), hsb);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        pos = 0;
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
        Keyboard.enableRepeatEvents(true);
        hovered = MouseUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5,getFinishedY() + 1, getWidth() - 10, getHeight() - 2);
        for (float i = 0; i + 1 < getWidth() - 10 ; i += 0.5f) {
            float posx = getFinishedX() + 5 + i;
            int color = Color.getHSBColor(i / getWidth(), saturation, brightness).getRGB();
            RenderUtil.drawRect(posx, getFinishedY() + 1, 1, getHeight() - 2, color);
            if (mouseX == posx) {
                if (pressedhue) {
                    colorSetting.setValue(color);
                    hue = i / getWidth();
                }
            }
            if (0.001 * Math.floor((i / getWidth()) * 1000.0) == 0.001 * Math.floor(hue* 1000.0)) pos = i;
        }
        Fonts.arialFont.drawStringWithShadow(getLabel(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - (Fonts.arialFont.getStringHeight(getLabel()) >>1),0xFFFFFFFF );
        RenderUtil.drawRect(getFinishedX() + 5 + pos, getFinishedY() + 1, 2, getHeight() - 2,  0xffffffff);
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (!hovered) return;
        switch (keyCode) {
            case Keyboard.KEY_UP:
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    if (brightness + 0.01 <= 1) brightness += 0.01;
                } else {
                    if (saturation + 0.01 <= 1) saturation += 0.01;
                }
                colorSetting.setValue(Color.HSBtoRGB(hue,saturation, brightness));
                break;
            case Keyboard.KEY_DOWN:
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    if (brightness - 0.01 >= 0) brightness -= 0.01;
                } else {
                    if (saturation - 0.01 >= 0) saturation -= 0.01;
                }
                colorSetting.setValue(Color.HSBtoRGB(hue,saturation, brightness));
                break;
            default:break;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = MouseUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5,getFinishedY() + 1, getWidth() - 10, getHeight() - 2);
        if (mouseButton == 0) {
            if (hovered) {
                pressedhue = true;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            if (pressedhue) {
                pressedhue = false;
            }
        }
    }

    public ColorSetting getColorSetting() {
        return colorSetting;
    }
}