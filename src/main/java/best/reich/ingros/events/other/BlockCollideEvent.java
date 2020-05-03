package best.reich.ingros.events.other;

import net.b0at.api.event.Event;
import net.minecraft.block.Block;

public final class BlockCollideEvent extends Event {

    private final Block block;

    public BlockCollideEvent(Block block) {
        this.block = block;
    }

    public final Block getBlock() {
        return this.block;
    }
}
