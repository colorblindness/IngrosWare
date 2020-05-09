package best.reich.ingros.module.modules.player;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.render.GuiInitEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.input.Keyboard;

@ModuleManifest(label = "InvWalk", category = ModuleCategory.PLAYER, color = 0xffffff00)
public class InvWalk extends ToggleableModule {
    private static final KeyBinding[] MOVEMENT_KEYS = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint};

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == EventType.PRE) {
            if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {

                mc.player.rotationYaw += Keyboard.isKeyDown(Keyboard.KEY_RIGHT) ? 4 : Keyboard.isKeyDown(Keyboard.KEY_LEFT) ? -4 : 0;

                mc.player.rotationPitch += (Keyboard.isKeyDown(Keyboard.KEY_DOWN) ? 4 : Keyboard.isKeyDown(Keyboard.KEY_UP) ? -4 : 0) * 0.75;

                mc.player.rotationPitch = MathHelper.clamp(mc.player.rotationPitch, -90, 90);

                runCheck();
            }
        }
    }

    @Subscribe
    public void onInit(GuiInitEvent event) {
        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
            runCheck();
        }
    }

    private void runCheck() {
        for (KeyBinding keyBinding : MOVEMENT_KEYS) {
            if (Keyboard.isKeyDown(keyBinding.getKeyCode())) {
                if (keyBinding.getKeyConflictContext() != KeyConflictContext.UNIVERSAL) {
                    keyBinding.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
                }
                KeyBinding.setKeyBindState(keyBinding.getKeyCode(), true);
            } else {
                KeyBinding.setKeyBindState(keyBinding.getKeyCode(), false);
            }
        }
    }
}
