package best.reich.ingros.mixin.impl;

import best.reich.ingros.mixin.accessors.ITextComponentString;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextComponentString.class)
public abstract class MixinTextComponentString implements ITextComponentString {

    @Accessor
    @Override
    public abstract void setText(String text);
}