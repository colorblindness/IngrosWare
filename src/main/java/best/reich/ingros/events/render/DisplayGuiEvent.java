package best.reich.ingros.events.render;

import net.b0at.api.event.Event;
import net.minecraft.client.gui.GuiScreen;


public class DisplayGuiEvent extends Event {

    private GuiScreen screen;

    public DisplayGuiEvent(GuiScreen screen) {
        this.screen = screen;
    }

    public GuiScreen getScreen() {
        return screen;
    }

    public void setScreen(GuiScreen screen) {
        this.screen = screen;
    }
}