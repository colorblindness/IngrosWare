package best.reich.ingros.mixin.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.other.EntityChunkEvent;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public class MixinWorldClient {

    @Inject(method = "onEntityAdded", at = @At("HEAD"))
    private void onEntityAdded(Entity entity, CallbackInfo info) {
        IngrosWare.INSTANCE.bus.fireEvent(new EntityChunkEvent(EventType.PRE, entity));
    }

    @Inject(method = "onEntityRemoved", at = @At("HEAD"))
    private void onEntityRemoved(Entity entity, CallbackInfo info) {
        IngrosWare.INSTANCE.bus.fireEvent(new EntityChunkEvent(EventType.POST, entity));
    }
}