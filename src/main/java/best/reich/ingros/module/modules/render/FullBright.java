package best.reich.ingros.module.modules.render;

import best.reich.ingros.events.entity.UpdateEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;

@ModuleManifest(label = "FullBright", category = ModuleCategory.RENDER, color = 0xFFA030FF)
public class FullBright extends ToggleableModule {
    private float oldGamma;
    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null || mc.gameSettings == null) return;
        mc.gameSettings.gammaSetting = 1000;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.world == null || mc.player == null) return;
        oldGamma = mc.gameSettings.gammaSetting;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.world == null || mc.player == null) return;
        mc.gameSettings.gammaSetting = oldGamma;
    }

}
