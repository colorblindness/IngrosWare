package best.reich.ingros.commands;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.util.logging.Logger;
import me.xenforu.kelo.command.Command;
import me.xenforu.kelo.command.annotation.CommandManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import org.lwjgl.input.Keyboard;

@CommandManifest(label = "Keybind", description = "set a module keybind!", handles = {"bind","key"})
public class KeybindCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length == 3) {
            String moduleName = args[1];
            ToggleableModule module = IngrosWare.INSTANCE.moduleManager.getToggleByName(moduleName);
            if (module != null) {
                int keyCode = Keyboard.getKeyIndex(args[2].toUpperCase());
                if (keyCode != -1) {
                    module.setBind(keyCode);
                    Logger.printMessage(module.getLabel() + " is now bound to \"" + Keyboard.getKeyName(keyCode) + "\".");
                } else {
                    Logger.printMessage("That is not a valid key code.");
                }
            } else {
                Logger.printMessage("That module does not exist.");
                Logger.printMessage("Type \"modules\" for a list of all modules.");
            }
        } else {
            Logger.printMessage("Invalid arguments.");
            Logger.printMessage("Usage: \"bind [module] [key]\"");
        }
    }
}
