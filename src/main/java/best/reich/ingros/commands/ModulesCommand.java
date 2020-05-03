package best.reich.ingros.commands;

import best.reich.ingros.IngrosWare;
import com.mojang.realmsclient.gui.ChatFormatting;
import best.reich.ingros.util.logging.Logger;
import me.xenforu.kelo.command.Command;
import me.xenforu.kelo.command.annotation.CommandManifest;
import net.minecraft.util.text.TextComponentString;

@CommandManifest(label = "Modules", description = "shows all the modules", handles = {"m", "plugins", "mods"})
public class ModulesCommand extends Command {

    @Override
    public void execute(String[] args) {
        final int size = IngrosWare.INSTANCE.moduleManager.getMap().values().size();
        TextComponentString message = new TextComponentString(ChatFormatting.PREFIX_CODE + "7Modules [" + size + "] " + ChatFormatting.PREFIX_CODE + "f ");
        IngrosWare.INSTANCE.moduleManager.getMap().values().forEach(module ->message.appendSibling(new TextComponentString((module.isEnabled() ? ChatFormatting.PREFIX_CODE + "a" : ChatFormatting.PREFIX_CODE + "c") + module.getLabel() + ChatFormatting.PREFIX_CODE + "7" + ", ")));
        Logger.printComponent(message);
    }
}
