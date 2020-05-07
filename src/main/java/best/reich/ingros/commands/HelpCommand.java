package best.reich.ingros.commands;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.util.logging.Logger;
import me.xenforu.kelo.command.Command;
import me.xenforu.kelo.command.annotation.CommandManifest;

@CommandManifest(label = "Help", description = "shows all commands", handles = {"h","commands"})
public class HelpCommand extends Command {

    @Override
    public void execute(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        IngrosWare.INSTANCE.commandManager.getValues().forEach(command ->
                stringBuilder.append(command.getLabel()).append(" - ").append(command.getDescription()).append("\n"));
        Logger.printMessage(stringBuilder.toString(),false);
    }
}
