package best.reich.ingros.mixin.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.render.CapeLocationEvent;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer extends AbstractClientPlayer {

    public MixinAbstractClientPlayer(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "getLocationCape",at = @At("HEAD"),cancellable = true)
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        CapeLocationEvent event = IngrosWare.INSTANCE.bus.fireEvent(new CapeLocationEvent(this));

    }

}
