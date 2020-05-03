package best.reich.ingros.manager;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.commands.*;
import best.reich.ingros.commands.module.ModuleCommand;
import best.reich.ingros.util.ClassUtil;
import me.xenforu.kelo.command.AbstractCommandManager;
import me.xenforu.kelo.command.Command;

import java.io.File;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
public class CommandManager extends AbstractCommandManager {

    @Override
    public void load() {
        register(new AltCommand());
        register(new FriendCommand());
        register(new HelpCommand());
        register(new KeybindCommand());
        register(new MacrosCommand());
        register(new ModulesCommand());
        register(new RelogCommand());
        register(new ToggleCommand());
        register(new PositionCommand());
        register(new BookCommand());
        loadExternalCommands();
        IngrosWare.INSTANCE.moduleManager.getValues().forEach(module -> {
            if (IngrosWare.INSTANCE.settingManager.getSettingsFromObject(module) != null)
                register(new ModuleCommand(module));
        });
    }

    @Override
    public void unload() {

    }

    private void loadExternalCommands() {
        try {
            final File dir = new File(IngrosWare.INSTANCE.path + File.separator + "externals" + File.separator + "commands");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (ClassUtil.getClassesEx(dir.getPath()).isEmpty()) System.out.println("[IngrosWare] No external commands found!");
            for (Class clazz : ClassUtil.getClassesEx(dir.getPath())) {
                if (clazz != null) {
                    if (Command.class.isAssignableFrom(clazz)) {
                        final Command command = (Command) clazz.newInstance();
                        if (command != null) {
                            register(command);
                            System.out.println("[IngrosWare] Found external command " + command.getLabel());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
