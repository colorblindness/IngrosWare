package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.network.PacketEvent;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.StringUtils;

@ModuleManifest(label = "Mentions", category = ModuleCategory.OTHER, color = 0xff007777, hidden = true)
public class Mentions extends ToggleableModule {

    private ResourceLocation pinglocal = new ResourceLocation("minecraft", "entity.experience_orb.pickup");
    private SoundEvent pingsound = new SoundEvent(pinglocal);

    @Setting("Chat")
    public boolean chat = true;

    @Setting("Sound")
    public boolean sound = true;


    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getType() == EventType.POST) {
            if (event.getPacket() instanceof SPacketChat) {
                SPacketChat packet = (SPacketChat) event.getPacket();
                String message = packet.chatComponent.getFormattedText();
                if (StringUtils.containsIgnoreCase(message, mc.session.getUsername())) {
                    if (chat)
                        packet.chatComponent = new TextComponentString(StringUtils.replaceIgnoreCase(message, mc.session.getUsername(), ChatFormatting.YELLOW + mc.session.getUsername() + ChatFormatting.RESET));
                    if (sound)
                        mc.player.playSound(pingsound, 1, 1);
                }
            }
        }
    }
}
