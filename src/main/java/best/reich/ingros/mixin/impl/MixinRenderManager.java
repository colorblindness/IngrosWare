package best.reich.ingros.mixin.impl;

import best.reich.ingros.mixin.accessors.IRenderManager;
import net.minecraft.client.renderer.entity.RenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager implements IRenderManager {
    @Override
    @Accessor
    public abstract double getRenderPosX();

    @Override
    @Accessor
    public abstract double getRenderPosY();

    @Override
    @Accessor
    public abstract double getRenderPosZ();
}