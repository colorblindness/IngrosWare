package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.util.logging.Logger;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

import java.util.HashMap;

@ModuleManifest(label = "TotemPopCounter", category = ModuleCategory.OTHER, color = 0xff40AEFE,hidden = true)
public class TotemPopCounter extends ToggleableModule {
    public static HashMap<String, Integer> popList = new HashMap<>();

     @Subscribe
    public void onUpdate(UpdateEvent event) {
         if (mc.world == null || mc.player == null) {
             return;
         }
        for(EntityPlayer player : mc.world.playerEntities) {
            if(player.getHealth() <= 0) {
                if(popList.containsKey(player.getName())) {
                    Logger.printMessage(ChatFormatting.DARK_AQUA + player.getName() + ChatFormatting.DARK_RED + " died after popping " + ChatFormatting.GOLD +  popList.get(player.getName()) + " totems!",true);
                    popList.remove(player.getName(), popList.get(player.getName()));
                }
            }
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.world == null || mc.player == null || event.getType() == EventType.PRE) {
            return;
        }
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35) {
                Entity entity = packet.getEntity(mc.world);
                if(popList == null) {
                    popList = new HashMap<>();
                }
                if(popList.get(entity.getName()) == null) {
                    popList.put(entity.getName(), 1);
                    Logger.printMessage(ChatFormatting.DARK_AQUA + entity.getName() + ChatFormatting.DARK_RED + " popped " + ChatFormatting.GOLD + "1 totem!",true);
                } else if(!(popList.get(entity.getName()) == null)) {
                    int popCounter = popList.get(entity.getName());
                    int newPopCounter = popCounter += 1;
                    popList.put(entity.getName(), newPopCounter);
                    Logger.printMessage(ChatFormatting.DARK_AQUA + entity.getName() + ChatFormatting.DARK_RED + " popped " + ChatFormatting.GOLD +newPopCounter + " totems!",true);
                }
            }
        }
    }
}