package best.reich.ingros.mixin.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.other.ChunkLoadEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketChunkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {

     @Inject(method = "handleChunkData", at = @At("HEAD"))
    private void handleChunkData(SPacketChunkData packetIn, CallbackInfo ci) {
        IngrosWare.INSTANCE.bus.fireEvent(new ChunkLoadEvent(Minecraft.getMinecraft().world.getChunkFromChunkCoords(packetIn.getChunkX(), packetIn.getChunkZ()),packetIn.isFullChunk(), packetIn.getChunkX(), packetIn.getChunkZ()));
    }
}
