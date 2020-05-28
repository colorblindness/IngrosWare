package best.reich.ingros.mixin.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.render.RenderChatBackgroundEvent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat {

    @Redirect(
            method = {"drawChat"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V",
                    ordinal = 0
            )
    )
    private void overrideChatBackgroundColour(int left, int top, int right, int bottom, int color) {
        RenderChatBackgroundEvent event = IngrosWare.INSTANCE.bus.fireEvent(new RenderChatBackgroundEvent());
        if (!event.isCancelled())
            Gui.drawRect(left, top, right, bottom, color);
    }
}
