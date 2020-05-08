package best.reich.ingros.events.render;

import net.b0at.api.event.Event;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;


public class RenderEntityEvent extends Event {

    private final Render renderer;
    private final Entity entity;
    private final double x, y, z;
    private final float entityYaw;
    private final float partialTicks;
    private final EventType eventType;
    public RenderEntityEvent(Render renderer, Entity entity, double x, double y, double z, float entityYaw, float partialTicks,EventType eventType) {
        this.renderer = renderer;
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.entityYaw = entityYaw;
        this.partialTicks = partialTicks;
        this.eventType = eventType;
    }

    public Render getRenderer() {
        return renderer;
    }

    public Entity getEntity() {
        return entity;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getEntityYaw() {
        return entityYaw;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public EventType getEventType() {
        return eventType;
    }
}
