package best.reich.ingros.events.other;

import net.b0at.api.event.Event;

public class FullScreenEvent extends Event {
    private float width,height;

    public FullScreenEvent(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
