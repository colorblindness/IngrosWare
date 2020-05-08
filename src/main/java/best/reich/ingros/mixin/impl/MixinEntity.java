package best.reich.ingros.mixin.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.EntityEvent;
import best.reich.ingros.events.other.SafeWalkEvent;
import best.reich.ingros.mixin.accessors.IEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Entity.class, priority = 1001)
public abstract class MixinEntity implements IEntity {
    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;
    @Shadow
    public boolean onGround;
    @Shadow
    public boolean inPortal;
    @Shadow
    public void move(MoverType type, double x, double y, double z) {}


    @Redirect(method = "applyEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void addVelocity(Entity entity, double x, double y, double z) {
        EntityEvent.EntityCollision event = new EntityEvent.EntityCollision(entity, x, y, z);
        IngrosWare.INSTANCE.bus.fireEvent(event);
        if(event.isCancelled()){
            return;
        }

        entity.motionX += x;
        entity.motionY += y;
        entity.motionZ += z;

        entity.isAirBorne = true;
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean isSneaking(Entity entity) {
        SafeWalkEvent event = new SafeWalkEvent();
        IngrosWare.INSTANCE.bus.fireEvent(event);
        return event.isCancelled() || entity.isSneaking();
    }
    @Override
    public float getRotationYaw() {
        return rotationYaw;
    }

    @Override
    public float getRotationPitch() {
        return rotationPitch;
    }

    @Override
    public double getPositionX() {
        return posX;
    }

    @Override
    public double getPositionY() {
        return posY;
    }

    @Override
    public double getPositionZ() {
        return posZ;
    }

    @Override
    public boolean getPositionOnGround() {
        return onGround;
    }

}
