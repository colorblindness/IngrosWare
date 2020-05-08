package best.reich.ingros.mixin.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.module.modules.render.Visuals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPlayer.class)
public class MixinModelPlayer {

    @Inject(method = "setRotationAngles", at = @At("RETURN"))
    public void setRotationAngles(float p_setRotationAngles_1_, float p_setRotationAngles_2_, float p_setRotationAngles_3_, float p_setRotationAngles_4_, float p_setRotationAngles_5_, float p_setRotationAngles_6_, Entity p_setRotationAngles_7_, CallbackInfo callbackInfo) {
        if (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null) {
            final Visuals esp = (Visuals) IngrosWare.INSTANCE.moduleManager.getModule("Visuals");
            if (esp.isEnabled() && esp.skeleton && p_setRotationAngles_7_ instanceof EntityPlayer) {
                esp.addEntity((EntityPlayer) p_setRotationAngles_7_, (ModelPlayer) (Object) this);
            }
        }
    }
}
