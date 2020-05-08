package best.reich.ingros.module.modules.movement;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.util.game.GameUtil;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;

@ModuleManifest(label = "Timer", category = ModuleCategory.MOVEMENT, color = 0xff0355ff)
public class Timer extends ToggleableModule {
    @Clamp(minimum = "0.1f", maximum = "10.0f")
    @Setting("Speed")
    public float speed = 10;

    @Subscribe
    public void onUpdatePre(UpdateEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.PRE) {
            GameUtil.setTimerSpeed(50 / speed);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        GameUtil.setTimerSpeed(50f);
    }
}
