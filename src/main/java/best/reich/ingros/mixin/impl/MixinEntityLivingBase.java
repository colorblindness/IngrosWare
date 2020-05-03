package best.reich.ingros.mixin.impl;

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
}
