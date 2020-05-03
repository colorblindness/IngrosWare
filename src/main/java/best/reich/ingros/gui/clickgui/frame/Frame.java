package best.reich.ingros.gui.clickgui.frame;

import best.reich.ingros.gui.clickgui.component.Component;
import best.reich.ingros.util.game.MouseUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

public class Frame {
    private final String label;
    private float posX;
    private float posY;
    private float lastPosX;
    private float lastPosY;
    private final float width;
    private final float height;
    private boolean extended,dragging;
    private final ArrayList<Component> components = new ArrayList<>();
    public Frame(String label, float posX, float posY, float width, float height) {
        this.label = label;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public void init() {
        components.forEach(Component::init);
    }

    public void moved(float posX,float posY) {
        components.forEach(component -> component.moved(posX,posY));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        if (isDragging()) {
            setPosX(mouseX + getLastPosX());
            setPosY(mouseY + getLastPosY());
            moved(getPosX(),getPosY());
        }
        if (getPosX() < 0) {
            setPosX(0);
            moved(getPosX(),getPosY());
        }
        if (getPosX() + getWidth() > scaledResolution.getScaledWidth()) {
            setPosX(scaledResolution.getScaledWidth() - getWidth());
            moved(getPosX(),getPosY());
        }
        if (getPosY() < 0) {
            setPosY(0);
            moved(getPosX(),getPosY());
        }
        if (getPosY() + getHeight() > scaledResolution.getScaledHeight()) {
            setPosY(scaledResolution.getScaledHeight() - getHeight());
            moved(getPosX(),getPosY());
        }
        if (isExtended()) getComponents().forEach(component -> component.drawScreen(mouseX, mouseY, partialTicks));
    }

    public void keyTyped(char character, int keyCode)  {
        if (isExtended()) getComponents().forEach(component -> component.keyTyped(character, keyCode));
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        final boolean hovered = MouseUtil.mouseWithinBounds(mouseX, mouseY, getPosX(),getPosY(),getWidth(),getHeight());
        switch (mouseButton) {
            case 0:
                if (hovered) {
                    setDragging(true);
                    setLastPosX(getPosX() - mouseX);
                    setLastPosY(getPosY() - mouseY);
                }
                break;
            case 1:
                if (hovered)
                    setExtended(!isExtended());
                break;
            default:
                break;
        }
        if (isExtended()) getComponents().forEach(component -> component.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isDragging()) setDragging(false);
        if (isExtended()) getComponents().forEach(component -> component.mouseReleased(mouseX, mouseY, mouseButton));
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public String getLabel() {
        return label;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getLastPosX() {
        return lastPosX;
    }

    public void setLastPosX(float lastPosX) {
        this.lastPosX = lastPosX;
    }

    public float getLastPosY() {
        return lastPosY;
    }

    public void setLastPosY(float lastPosY) {
        this.lastPosY = lastPosY;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }
}
