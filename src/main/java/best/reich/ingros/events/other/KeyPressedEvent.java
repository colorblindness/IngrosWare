package best.reich.ingros.events.other;

import net.b0at.api.event.Event;

public class KeyPressedEvent extends Event {

    private final int key;

    public KeyPressedEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}