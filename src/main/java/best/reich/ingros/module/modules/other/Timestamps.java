package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.mixin.accessors.ITextComponentString;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;

import java.text.SimpleDateFormat;
import java.util.Date;


@ModuleManifest(label = "Timestamps", category = ModuleCategory.OTHER, color = 0xFEFEEFFD)
public class Timestamps extends ToggleableModule {

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.POST) {
            if (event.getPacket() instanceof SPacketChat) {
                final SPacketChat sPacketChat = (SPacketChat) event.getPacket();
                if (sPacketChat.getChatComponent() instanceof TextComponentString) {
                    final ITextComponentString textComponentString = (ITextComponentString) sPacketChat.getChatComponent();
                    textComponentString.setText(ChatFormatting.RED + "<" + ChatFormatting.GRAY + new SimpleDateFormat("h:mm a").format(new Date()) + ChatFormatting.RED + "> " + ((TextComponentString) sPacketChat.getChatComponent()).getText());
                }
            }
        }
    }
}
