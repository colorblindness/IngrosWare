package best.reich.ingros.module.modules.other;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.util.logging.Logger;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Setting;
import me.xenforu.kelo.util.math.TimerUtil;
import net.b0at.api.event.Subscribe;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ModuleManifest(label = "Spammer", category = ModuleCategory.OTHER, color = 0xff0355ff)
public class Spammer extends ToggleableModule {
    @Clamp(minimum = "1.0", maximum = "60.0")
    @Setting("Speed")
    public float delay = 5.0f;
    private final TimerUtil timer = new TimerUtil();
    private int index = 0;
    private List<String> StringList;
    private final Random random = new Random();
    private boolean FileInit = false;

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.world == null || mc.player == null | !FileInit) return;
        if (timer.sleep((int) (delay * 1000))) {
            if (index < StringList.size()) {
                final ArrayList<NetworkPlayerInfo> niggas = new ArrayList<>(mc.player.connection.getPlayerInfoMap());
                final String msg = StringList.get(index).replace("%RANDOMPLAYER%",niggas.size() < 2 ? "": niggas.get(random.nextInt(niggas.size())).getGameProfile().getName()).replace("%INVISIBLE%","\u061C").replace("%RANDOMNUMBER%", String.valueOf(100000 + new Random().nextInt(999999)));
                mc.player.connection.getNetworkManager().sendPacket(new CPacketChatMessage(msg));
                index++;
            } else index = 0;
            timer.reset();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (FileInit) {
            StringList.clear();
            StringList = null;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        File file = new File(IngrosWare.INSTANCE.path + File.separator + "spam.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
                StringList = new ArrayList<>();
                StringList.add("IngrosWare owns all! %RANDOMNUMBER%");
                FileInit = true;
                return;
            }

            if (file.exists()) {
                if (Files.readAllLines(file.toPath()).size() <= 0) {
                    StringList = new ArrayList<>();
                    StringList.add("IngrosWare owns all! %RANDOMNUMBER%");
                    FileInit = true;
                    return;
                }

                List<String> lines = Files.readAllLines(file.toPath());
                if (lines.size() > 0) {
                    StringList = lines;
                    FileInit = true;
                    return;
                }
            }
        } catch (IOException ignored) {

        }
        index = 0;
    }
}
