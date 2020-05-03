package best.reich.ingros.gui.clickgui;

import best.reich.ingros.gui.clickgui.frame.Frame;
import best.reich.ingros.gui.clickgui.frame.impl.CategoryFrame;
import me.xenforu.kelo.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;
import java.util.ArrayList;

public class ClickGUI extends GuiScreen {
    private final ArrayList<Frame> frames = new ArrayList<>();
    public void init() {
        int x = 2;
        int y = 2;
        for (ModuleCategory moduleCategory : ModuleCategory.values()) {
            getFrames().add(new CategoryFrame(moduleCategory, x, y, 110, 16));
            if (x + 235 >= new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth()) {
                x = 2;
                y += 20;
            } else x += 115;
        }
        getFrames().forEach(Frame::init);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        getFrames().forEach(frame -> frame.drawScreen(mouseX,mouseY,partialTicks));
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        super.keyTyped(character, keyCode);
        getFrames().forEach(frame -> frame.keyTyped(character,keyCode));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        getFrames().forEach(frame -> frame.mouseClicked(mouseX,mouseY,mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        getFrames().forEach(frame -> frame.mouseReleased(mouseX,mouseY,mouseButton));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public ArrayList<Frame> getFrames() {
        return frames;
    }
}
