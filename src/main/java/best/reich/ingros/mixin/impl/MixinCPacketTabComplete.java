package best.reich.ingros.mixin.impl;

import best.reich.ingros.mixin.accessors.ICPacketTabComplete;
import net.minecraft.network.play.client.CPacketTabComplete;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketTabComplete.class)
public abstract class MixinCPacketTabComplete implements ICPacketTabComplete {

    @Override
    @Accessor
    public abstract void setMessage(String message);

}
