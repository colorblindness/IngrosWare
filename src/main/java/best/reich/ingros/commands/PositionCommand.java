package best.reich.ingros.commands;

import best.reich.ingros.util.logging.Logger;
import me.xenforu.kelo.command.Command;
import me.xenforu.kelo.command.annotation.CommandManifest;
import me.xenforu.kelo.util.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;

@CommandManifest(label = "Position", description = "teleport", handles = {"position", "pos", "p"})
public class PositionCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length > 2) {
            double distance = Double.parseDouble(args[2]);
            switch (args[1]) {
                case "v":
                case "vclip":

                    mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY + distance, mc.player.posZ);
                    Logger.printMessage("Vclipped " + distance,true);
                    break;
                case "h":
                case "hclip":
                    final Vec3d dir = MathUtil.direction(Minecraft.getMinecraft().player.rotationYaw);
                    if (dir != null) {
                        if (Minecraft.getMinecraft().player.getRidingEntity() != null) {
                            Minecraft.getMinecraft().player.getRidingEntity().setPosition(Minecraft.getMinecraft().player.getRidingEntity().posX + dir.x * distance, Minecraft.getMinecraft().player.getRidingEntity().posY, Minecraft.getMinecraft().player.getRidingEntity().posZ + dir.z * distance);
                        } else {
                            Minecraft.getMinecraft().player.setPosition(Minecraft.getMinecraft().player.posX + dir.x * distance, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ + dir.z * distance);
                        }
                        Logger.printMessage("Teleported you " + ((distance > 0) ? "forward" : "backward") + " " + distance,true);
                    }
                    break;
                default:
                    break;
            }
        } else {
            Logger.printMessage("Invalid arguments! usage position v/h <ammount>",true);
        }

    }
}
