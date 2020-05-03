package best.reich.ingros.mixin.accessors;

import net.minecraft.util.Session;

public interface IMinecraft {

    void setSession(Session session);

    void setRightClickDelayTimer(int delay);
}
