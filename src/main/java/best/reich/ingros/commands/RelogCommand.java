package best.reich.ingros.commands;

import best.reich.ingros.util.logging.Logger;
import me.xenforu.kelo.command.Command;
import me.xenforu.kelo.command.annotation.CommandManifest;
import net.minecraft.client.multiplayer.GuiConnecting;

import java.util.Objects;

@CommandManifest(label = "Relog", description = "relog into the server you are connected to", handles = {"r", "rel"})
public class RelogCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (mc.world == null) return;
        if (mc.isSingleplayer()) {
            Logger.printMessage("Cannot relog into a singleplayer server!",true);
            return;
        }
        mc.world.sendQuittingDisconnectingPacket();
        mc.displayGuiScreen(new GuiConnecting(null, mc, Objects.requireNonNull(mc.getCurrentServerData())));
    }
}
