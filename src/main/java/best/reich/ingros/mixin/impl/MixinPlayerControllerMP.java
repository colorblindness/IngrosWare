package best.reich.ingros.mixin.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.other.ClickBlockEvent;
import best.reich.ingros.events.other.DamageBlockEvent;
import best.reich.ingros.events.other.ResetBlockRemovingEvent;
import best.reich.ingros.mixin.accessors.IPlayerControllerMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP implements IPlayerControllerMP {

    @Accessor
    @Override
    public abstract void setBlockHitDelay(int blockHitDelay);

    @Accessor
    @Override
    public abstract void setCurBlockDamageMP(float curBlockDamageMP);

    @Accessor
    @Override
    public abstract float getCurBlockDamageMP();

    @Inject(method = "clickBlock", at = @At("HEAD"))
    private void clickBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> cir) {
        IngrosWare.INSTANCE.bus.fireEvent(new ClickBlockEvent(loc, face));
    }

    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"))
    private void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> cir) {
        IngrosWare.INSTANCE.bus.fireEvent(new DamageBlockEvent(posBlock, directionFacing));
    }

    @Inject(method = "resetBlockRemoving", at = @At("HEAD"))
    public void onResetBlockRemoving(CallbackInfo ci) {
        final ResetBlockRemovingEvent event = new ResetBlockRemovingEvent();
        IngrosWare.INSTANCE.bus.fireEvent(event);
        return;
    }
}