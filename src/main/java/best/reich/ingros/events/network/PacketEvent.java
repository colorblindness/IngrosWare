package best.reich.ingros.events.network;

import net.b0at.api.event.types.EventType;
import net.b0at.api.event.Event;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {
    private Packet packet;
    private EventType type;

    public PacketEvent(EventType type, Packet packet) {
        this.packet = packet;
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public Packet getPacket() {
        return packet;
    }
}
