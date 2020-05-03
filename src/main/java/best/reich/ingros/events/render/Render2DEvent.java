package best.reich.ingros.events.render;

import net.b0at.api.event.Event;
import net.minecraft.client.gui.ScaledResolution;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
public class Render2DEvent extends Event {
    private final float partialTicks;
    private final ScaledResolution scaledResolution;
    public Render2DEvent(float partialTicks,ScaledResolution scaledResolution) {
        this.partialTicks = partialTicks;
        this.scaledResolution= scaledResolution;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }
}
