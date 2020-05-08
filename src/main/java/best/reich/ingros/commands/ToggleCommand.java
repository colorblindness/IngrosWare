package best.reich.ingros.commands;

import best.reich.ingros.IngrosWare;
import com.mojang.realmsclient.gui.ChatFormatting;
import best.reich.ingros.util.logging.Logger;
import me.xenforu.kelo.command.Command;
import me.xenforu.kelo.command.annotation.CommandManifest;
import me.xenforu.kelo.module.type.ToggleableModule;

@CommandManifest(label = "Toggle", description = "toggles a module", handles = {"t"})
public class ToggleCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            Logger.printMessage("Too little arguments!", true);
            return;
        }
        ToggleableModule module = IngrosWare.INSTANCE.moduleManager.getToggleByName(args[1]);
        if (module != null) {
            module.toggle();
            Logger.printMessage(module.getLabel() + " has been " + ChatFormatting.PREFIX_CODE + (module.getState() ? "aenabled" : "cdisabled"), true);
        } else {
            Logger.printMessage("Not a mod nigga", true);
        }
    }
}
