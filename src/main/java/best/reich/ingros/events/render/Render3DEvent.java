package best.reich.ingros.events.render;

import net.b0at.api.event.Event;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
public class Render3DEvent extends Event {
    private final float partialTicks;

    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
