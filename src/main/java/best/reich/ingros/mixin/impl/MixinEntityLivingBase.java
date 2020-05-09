package best.reich.ingros.mixin.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.JumpEvent;
import best.reich.ingros.mixin.accessors.IEntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity implements IEntityLivingBase {

    @Shadow protected boolean isJumping;

    @Shadow
    public abstract IAttributeInstance getEntityAttribute(IAttribute p_getEntityAttribute_1_);
    @Shadow
    public abstract ItemStack getHeldItemMainhand();
    @Shadow
    public abstract boolean isOnLadder();
    @Shadow
    public abstract boolean isPotionActive(Potion p_isPotionActive_1_);
    @Shadow
    public abstract ItemStack getHeldItem(EnumHand p_getHeldItem_1_);
    @Shadow
    public abstract void setSprinting(boolean p_setSprinting_1_);
    @Shadow
    public abstract void setLastAttackedEntity(Entity p_setLastAttackedEntity_1_);
    @Shadow
    public abstract void setHeldItem(EnumHand p_setHeldItem_1_, ItemStack p_setHeldItem_2_);

    @Accessor
    @Override
    public abstract boolean getIsJumping();

    private float x,y,z;

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo callbackInfo) {
        JumpEvent event = new JumpEvent(getX(),getY(),getZ());
        IngrosWare.INSTANCE.bus.fireEvent(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Redirect(method = "jump", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;motionX:D"))
    private double jumpMotionX(Entity player) {
        return getX();
    }

    @Redirect(method = "jump", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;motionY:D"))
    private double jumpMotionY(Entity player) {
        return getY();
    }

    @Redirect(method = "jump", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;motionZ:D"))
    private double jumpMotionZ(Entity player) {
        return getZ();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}
