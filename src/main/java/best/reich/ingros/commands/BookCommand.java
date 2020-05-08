package best.reich.ingros.commands;

import best.reich.ingros.util.logging.Logger;
import io.netty.buffer.Unpooled;
import me.xenforu.kelo.command.Command;
import me.xenforu.kelo.command.annotation.CommandManifest;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@CommandManifest(label = "Book", description = "make a glitched book", handles = {"b","book","bookdupe","bd"})
public class BookCommand extends Command {

    @Override
    public void execute(String[] args) {
        ItemStack heldItem = mc.player.getHeldItemMainhand();
        if (heldItem.getItem() == Items.WRITABLE_BOOK) {
            final int limit = 50;
            Random rand = new Random();
            IntStream characterGenerator = null;
            characterGenerator = rand.ints(128, 1112063).map(i -> (i < 55296) ? i : (i + 2048));
            String joinedPages = characterGenerator.limit(50 * 210).mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());
            NBTTagList pages = new NBTTagList();
            for (int page = 0; page < limit; ++page) {
                pages.appendTag(new NBTTagString(joinedPages.substring(page * 210, (page + 1) * 210)));
            }
            if (heldItem.hasTagCompound()) {
                heldItem.getTagCompound().setTag("pages", pages);
            }
            else {
                heldItem.setTagInfo("pages", pages);
            }
            StringBuilder stackName = new StringBuilder();
            for(int i = 0; i < 16; i++)
                stackName.append("\u0014\f");

            heldItem.setTagInfo("author", new NBTTagString(mc.player.getName()));
            heldItem.setTagInfo(
                    "title",
                    new NBTTagString(stackName.toString()));

            PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
            buf.writeItemStack(heldItem);
            mc.player.connection.sendPacket(new CPacketCustomPayload("MC|BSign", buf));
            Logger.printMessage("Book Hack Success!",true);
        } else {
            Logger.printMessage("Pussy nigga u fucked up!",true);
        }

    }
}
