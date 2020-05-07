package best.reich.ingros.commands;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.util.logging.Logger;
import me.xenforu.kelo.command.Command;
import me.xenforu.kelo.command.annotation.CommandManifest;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@CommandManifest(label = "Macros", description = "add or remove a macro", handles = {"mac","macro"})
public class MacrosCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            switch (args[1].toLowerCase()) {
                case "list":
                    if (IngrosWare.INSTANCE.macroManager.getValues().isEmpty()) {
                        Logger.printMessage("Your macro list is empty.",false);
                        return;
                    }
                    Logger.printMessage("Your macros are:",false);
                    IngrosWare.INSTANCE.macroManager.getValues().forEach(macro ->
                            Logger.printMessage("Label: " + macro.getLabel() + ", Keybind: " + Keyboard.getKeyName(macro.getKey()) + ", Text: " + macro.getText() + ".",false));
                    break;
                case "reload":
                    IngrosWare.INSTANCE.macroManager.clear();
                    IngrosWare.INSTANCE.macroManager.load();
                    Logger.printMessage("Reloaded macros.",true);
                    break;
                case "remove":
                case "delete":
                    if (args.length < 3) {
                        Logger.printMessage("Invalid args.",true);
                        return;
                    }
                    if (IngrosWare.INSTANCE.macroManager.isMacro(args[2])) {
                        IngrosWare.INSTANCE.macroManager.remove(args[2]);
                        Logger.printMessage("Removed a macro named " + args[2] + ".",true);
                        if (IngrosWare.INSTANCE.macroManager.getMacroFile().exists()) {
                            IngrosWare.INSTANCE.macroManager.unload();
                        } else {
                            try {
                                IngrosWare.INSTANCE.macroManager.getMacroFile().createNewFile();
                                IngrosWare.INSTANCE.macroManager.unload();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Logger.printMessage(args[2] + " is not a macro.",true);
                    }
                    break;
                case "clear":
                    if (IngrosWare.INSTANCE.macroManager.getValues().isEmpty()) {
                        Logger.printMessage("Your macro list is empty.",true);
                        return;
                    }
                    Logger.printMessage("Cleared all macros.",true);
                    IngrosWare.INSTANCE.macroManager.clear();
                    if (IngrosWare.INSTANCE.macroManager.getMacroFile().exists()) {
                        IngrosWare.INSTANCE.macroManager.unload();
                    } else {
                        try {
                            IngrosWare.INSTANCE.macroManager.getMacroFile().createNewFile();
                            IngrosWare.INSTANCE.macroManager.unload();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "add":
                case "create":
                    if (args.length < 5) {
                        Logger.printMessage("Invalid args.",true);
                        return;
                    }
                    int keyCode = Keyboard.getKeyIndex(args[3].toUpperCase());
                    if (keyCode != -1 && !Keyboard.getKeyName(keyCode).equals("NONE")) {
                        if (IngrosWare.INSTANCE.macroManager.getMacroByKey(keyCode) != null) {
                            Logger.printMessage("There is already a macro bound to that key.",true);
                            return;
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 4; i < args.length; i++) {
                            stringBuilder.append(args[i]);
                            if (i != args.length - 1) stringBuilder.append(" ");
                        }
                        IngrosWare.INSTANCE.macroManager.addMacro(args[2], keyCode, stringBuilder.toString());
                        Logger.printMessage("Bound a macro named " + args[2] + " to the key " + Keyboard.getKeyName(keyCode) + ".",true);
                        if (IngrosWare.INSTANCE.macroManager.getMacroFile().exists()) {
                            IngrosWare.INSTANCE.macroManager.unload();
                        } else {
                            try {
                                IngrosWare.INSTANCE.macroManager.getMacroFile().createNewFile();
                                IngrosWare.INSTANCE.macroManager.unload();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Logger.printMessage("That is not a valid key code.",true);
                    }
                    break;
            }
        } else Logger.printMessage("Not enough arguments!",true);
    }
}
