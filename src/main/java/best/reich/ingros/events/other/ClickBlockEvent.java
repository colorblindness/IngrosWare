package best.reich.ingros.events.other;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.b0at.api.event.Event;

public class ClickBlockEvent extends Event {

    private final BlockPos pos;
    private final EnumFacing facing;

    public ClickBlockEvent(BlockPos pos, EnumFacing facing) {
        this.pos = pos;
        this.facing = facing;
    }

    public BlockPos getPos() {
        return pos;
    }

    public EnumFacing getFacing() {
        return facing;
    }
}