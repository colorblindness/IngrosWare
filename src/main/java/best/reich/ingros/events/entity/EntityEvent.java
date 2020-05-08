package best.reich.ingros.events.entity;

import net.b0at.api.event.Event;
import net.minecraft.entity.Entity;

/**
 * No idea where this is from, what do I look like? A developer? -Zim 5/5/20
 */
public class EntityEvent extends Event {

    private Entity entity;

    public EntityEvent(Entity entity) {
        super();
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public static class EntityCollision extends EntityEvent {
        double x, y, z;

        public EntityCollision(Entity entity, double x, double y, double z) {
            super(entity);
            this.x = x;
            this.y = y;
            this.z = z;
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

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void setZ(double z) {
            this.z = z;
        }
    }
}
