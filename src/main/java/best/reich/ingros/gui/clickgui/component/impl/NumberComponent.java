package best.reich.ingros.gui.clickgui.component.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.gui.clickgui.component.Component;
import best.reich.ingros.module.modules.render.ClickGui;
import best.reich.ingros.util.game.MouseUtil;
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.setting.impl.NumberSetting;
import me.xenforu.kelo.util.font.Fonts;
import me.xenforu.kelo.util.math.MathUtil;
import net.minecraft.util.math.MathHelper;

public class NumberComponent extends Component {
    private final NumberSetting<Number> numberSetting;
    private boolean sliding;

    public NumberComponent(NumberSetting<Number> numberSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(numberSetting.getLabel(), posX, posY, offsetX, offsetY, width, height);
        this.numberSetting = numberSetting;
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
        Fonts.arialFont.drawStringWithShadow(getLabel() + ": " + getNumberSetting().getValue(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - Fonts.arialFont.getStringHeight(getLabel()) / 2, 0xFFFFFFFF);
        float length = MathHelper.floor(((getNumberSetting().getValue()).floatValue() - getNumberSetting().getMinimum().floatValue()) / (getNumberSetting().getMaximum().floatValue() - getNumberSetting().getMinimum().floatValue()) * (getWidth() - 10));
        RenderUtil.drawRect(getFinishedX() + 5, getFinishedY() + getHeight() - 2, length, 2, clickGui.color.getRGB());
        if (sliding) {
            if (getNumberSetting().getValue() instanceof Float) {
                float preval = ((mouseX - (getFinishedX() + 5)) * (getNumberSetting().getMaximum().floatValue() - getNumberSetting().getMinimum().floatValue()) / (getWidth() - 10) + getNumberSetting().getMinimum().floatValue());
                getNumberSetting().setValue(MathUtil.roundFloat(preval, 2));
            } else if (getNumberSetting().getValue() instanceof Integer) {
                int preval = (int) ((mouseX - (getFinishedX() + 5)) * (getNumberSetting().getMaximum().intValue() - getNumberSetting().getMinimum().intValue()) / (getWidth() - 10) + getNumberSetting().getMinimum().intValue());
                getNumberSetting().setValue(preval);
            } else if (getNumberSetting().getValue() instanceof Double) {
                double preval = ((mouseX - (getFinishedX() + 5)) * (getNumberSetting().getMaximum().doubleValue() - getNumberSetting().getMinimum().doubleValue()) / (getWidth() - 10) + getNumberSetting().getMinimum().doubleValue());
                getNumberSetting().setValue(MathUtil.roundDouble(preval, 2));
            } else if (getNumberSetting().getValue() instanceof Long) {
                long preval = (long) ((mouseX - (getFinishedX() + 5)) * (getNumberSetting().getMaximum().doubleValue() - getNumberSetting().getMinimum().doubleValue()) / (getWidth() - 10) + getNumberSetting().getMinimum().doubleValue());
                getNumberSetting().setValue(MathUtil.roundDouble(preval, 2));
            }
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(int mx, int my, int button) {
        super.mouseClicked(mx, my, button);
        if ((MouseUtil.mouseWithinBounds(mx, my, getFinishedX(), getFinishedY(), getWidth(), getHeight())) && button == 0)
            setSliding(true);
    }

    @Override
    public void mouseReleased(int mx, int my, int button) {
        super.mouseReleased(mx, my, button);
        if (isSliding()) setSliding(false);
    }

    public void setSliding(boolean sliding) {
        this.sliding = sliding;
    }

    public NumberSetting<Number> getNumberSetting() {
        return numberSetting;
    }

    public boolean isSliding() {
        return sliding;
    }
}
