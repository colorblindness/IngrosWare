package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.network.PacketEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Setting;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;

@ModuleManifest(label = "NoLag", category = ModuleCategory.OTHER, color = 0x6FA96E,hidden = true)
public class NoLag extends ToggleableModule {

    @Setting("NoSound")
    public boolean soundlag = true;

    @Setting("Sign")
    public boolean sign;

    @Setting("Book")
    public boolean antiBookBan;

    @Setting("NoParticles")
    public boolean noparticles;

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.world != null) mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.world != null) mc.renderGlobal.loadRenderers();
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.PRE) {
            if (mc.player != null) {
                if (antiBookBan) {
                    for (int i = 0; i <= 45; i++) {
                        ItemStack item = mc.player.inventory.getStackInSlot(i);
                        if (item.getItem() instanceof ItemBook) {
                            mc.player.dropItem(item, false);
                        }
                    }
                }
                if (sign) {
                    for (TileEntity e : mc.world.loadedTileEntityList) {
                        if (e instanceof TileEntitySign) {
                            TileEntitySign sign = (TileEntitySign) e;

                            for (int i = 0; i <= 3; i++) {
                                sign.signText[i] = new TextComponentString("");
                            }
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.player == null) return;
        if (event.getType() == EventType.POST) {
            if (noparticles && event.getPacket() instanceof SPacketParticles || event.getPacket() instanceof SPacketEffect) {
                event.setCancelled(true);
            }
            if (soundlag && event.getPacket() instanceof SPacketSoundEffect) {
                SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
                if (packet.getCategory() == SoundCategory.PLAYERS && packet.getSound() == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
