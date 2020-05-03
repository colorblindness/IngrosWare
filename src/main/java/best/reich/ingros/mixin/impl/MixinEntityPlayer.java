package best.reich.ingros.mixin.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.other.OpaqueEvent;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntity {

    @Inject(method = "isEntityInsideOpaqueBlock",at = @At("HEAD"),cancellable = true)
    private void onIsEntityInsideOpaqueBlock(CallbackInfoReturnable<Boolean> cir) {
        OpaqueEvent insideBlockRenderEvent = new OpaqueEvent();
        IngrosWare.INSTANCE.bus.fireEvent(insideBlockRenderEvent);
        if(insideBlockRenderEvent.isCancelled()) {
            cir.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "attackTargetEntityWithCurrentItem", constant = { @Constant(doubleValue = 0.6) })
    private double decelerate(final double original) {
        return 1.0;
    }

}
