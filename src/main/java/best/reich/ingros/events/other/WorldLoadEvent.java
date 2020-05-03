package best.reich.ingros.events.other;

import net.b0at.api.event.Event;
import net.minecraft.client.multiplayer.WorldClient;

public class WorldLoadEvent extends Event {
    private WorldClient worldClient;

    public WorldLoadEvent(WorldClient worldClient) {
        this.worldClient = worldClient;
    }

    public WorldClient getWorldClient() {
        return worldClient;
    }
}
