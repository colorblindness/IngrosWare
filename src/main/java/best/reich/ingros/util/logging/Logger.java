package best.reich.ingros.util.logging;

import best.reich.ingros.IngrosWare;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.xenforu.kelo.traits.Minecraftable;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextComponentString;

public class Logger implements Minecraftable {

    public static void printComponent(TextComponentString textComponent) {
        mc.ingameGUI.getChatGUI().printChatMessage(textComponent);
    }

    public static void printMessage(String message,boolean notification) {
        mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(ChatFormatting.RED + "<" + ChatFormatting.GRAY + "IngrosWare" + ChatFormatting.RED + "> " + ChatFormatting.GRAY +  message));
        if (notification)
        IngrosWare.INSTANCE.notificationManager.addNotification(StringUtils.stripControlCodes(message),3000);
    }
}
