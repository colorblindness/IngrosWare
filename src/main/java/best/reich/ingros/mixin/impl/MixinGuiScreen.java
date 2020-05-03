package best.reich.ingros.mixin.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.render.GuiInitEvent;
import best.reich.ingros.events.render.RenderToolTipEvent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {

    @Shadow
    protected FontRenderer fontRenderer;

    @Inject(method = "initGui",at = @At("HEAD"))
    public void initGui(CallbackInfo ci) {
        IngrosWare.INSTANCE.bus.fireEvent(new GuiInitEvent());
    }


    @Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
    public void renderToolTip(ItemStack stack, int x, int y, CallbackInfo info) {
        final RenderToolTipEvent event = new RenderToolTipEvent(stack,x,y);
        IngrosWare.INSTANCE.bus.fireEvent(event);

        if(event.isCancelled()) {
            info.cancel();
        }
    }

}
