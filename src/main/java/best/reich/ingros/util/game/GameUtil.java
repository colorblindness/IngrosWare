package best.reich.ingros.util.game;

import me.xenforu.kelo.traits.Minecraftable;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;

public class GameUtil implements Minecraftable {
    public static void setTimerSpeed(float timer) {
        mc.timer.tickLength = timer;
    }
    public static void setRightClickDelayTimer(int rightClickDelayTimer) {
        mc.rightClickDelayTimer = rightClickDelayTimer;
    }
}
