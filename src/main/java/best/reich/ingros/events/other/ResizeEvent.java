package best.reich.ingros.events.other;

import net.b0at.api.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class ResizeEvent extends Event {
    private ScaledResolution sr;

    public ResizeEvent(ScaledResolution sr) {
        this.sr = sr;
    }

    public ScaledResolution getSr() {
        return sr;
    }
}

