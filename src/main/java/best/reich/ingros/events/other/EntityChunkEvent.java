package best.reich.ingros.events.other;

import net.b0at.api.event.types.EventType;
import net.minecraft.entity.Entity;
import net.b0at.api.event.Event;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
public class EntityChunkEvent extends Event {
    private final Entity entity;
    private EventType type;

    public EntityChunkEvent(EventType type, Entity entity) {
        this.entity = entity;
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public Entity getEntity() {
        return entity;
    }

}
