package best.reich.ingros.mixin.accessors;

import net.minecraft.network.PacketBuffer;

public interface ICPacketCustomPayload {

    void setData(PacketBuffer data);

}