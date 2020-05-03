package best.reich.ingros.mixin.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.MotionEvent;
import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.entity.UpdateInputEvent;
import best.reich.ingros.events.other.PushOutOfBlocksEvent;
import best.reich.ingros.mixin.accessors.IPlayerSP;
import net.b0at.api.event.types.EventType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends MixinEntityPlayer implements IPlayerSP {

    @Shadow
    protected Minecraft mc;

    @Override
    public void move(MoverType type, double x, double y, double z) {
        MotionEvent event = new MotionEvent(x, y, z);
        IngrosWare.INSTANCE.bus.fireEvent(event);
        super.move(type, event.getX(), event.getY(), event.getZ());
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;handleMovement(Lnet/minecraft/util/MovementInput;)V"))
    private void onLivingUpdate(CallbackInfo ci) {
        IngrosWare.INSTANCE.bus.fireEvent(new UpdateInputEvent());
    }



    @Inject(method = "pushOutOfBlocks",at = @At("HEAD"),cancellable = true)
    private void onPushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        PushOutOfBlocksEvent eventPushOutOfBlocks = new PushOutOfBlocksEvent();
        IngrosWare.INSTANCE.bus.fireEvent(eventPushOutOfBlocks);

        if(eventPushOutOfBlocks.isCancelled()) {
            cir.setReturnValue(false);
        }
    }

    private UpdateEvent eventUpdate;
    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"))
    private void onUpdateWalkingPlayerHead(CallbackInfo ci) {
        eventUpdate = new UpdateEvent(EventType.PRE, (EntityPlayerSP) (Object) this, getRotationYaw(),getRotationPitch(),getPositionX(),getPositionY(),getPositionZ(),getPositionOnGround());
        IngrosWare.INSTANCE.bus.fireEvent(eventUpdate);
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;posX:D"))
    private double onUpdateWalkingPlayerPosX(EntityPlayerSP player) {
        return eventUpdate.getX();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/AxisAlignedBB;minY:D"))
    private double onUpdateWalkingPlayerMinY(AxisAlignedBB boundingBox) {
        return eventUpdate.getY();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;posZ:D"))
    private double onUpdateWalkingPlayerPosZ(EntityPlayerSP player) {
        return eventUpdate.getZ();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;onGround:Z"))
    private boolean onUpdateWalkingPlayerOnGround(EntityPlayerSP player) {
        return eventUpdate.isOnGround();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;rotationYaw:F"))
    private float onUpdateWalkingPlayerRotationYaw(EntityPlayerSP player) {
        return eventUpdate.getYaw();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;rotationPitch:F"))
    private float onUpdateWalkingPlayerRotationPitch(EntityPlayerSP player) {
        return eventUpdate.getPitch();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    private void onUpdateWalkingPlayerReturn(CallbackInfo ci) {
        IngrosWare.INSTANCE.bus.fireEvent(new UpdateEvent());
    }

    @Override
    public void setInPortal(boolean inPortal) {
        this.inPortal = inPortal;
    }

    @Override
    public boolean isInLiquid() {
        for (int x = MathHelper.floor(Minecraft.getMinecraft().player.getEntityBoundingBox().minX); x < MathHelper.floor(Minecraft.getMinecraft().player.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int z = MathHelper.floor(Minecraft.getMinecraft().player.getEntityBoundingBox().minZ); z < MathHelper.floor(Minecraft.getMinecraft().player.getEntityBoundingBox().maxZ) + 1; ++z) {
                final BlockPos pos = new BlockPos(x, (int) Minecraft.getMinecraft().player.getEntityBoundingBox().minY, z);
                final Block block = Minecraft.getMinecraft().world.getBlockState(pos).getBlock();
                if (!(block instanceof BlockAir)) {
                    return block instanceof BlockLiquid;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isOnLiquid() {
        if (mc.player == null)
            return false;
        boolean onLiquid = false;
        int y = (int) mc.player.getEntityBoundingBox().offset(0.0D, -0.01D, 0.0D).minY;
        for (int x = MathHelper.floor(mc.player.getEntityBoundingBox().minX); x < MathHelper
                .floor(mc.player.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor(mc.player.getEntityBoundingBox().minZ); z < MathHelper
                    .floor(mc.player.getEntityBoundingBox().maxZ) + 1; z++) {
                Block block = mc.world.getBlockState(new BlockPos(x, y, z))
                        .getBlock();
                if ((block != null) && (!(block instanceof BlockAir))) {
                    if (!(block instanceof BlockLiquid))
                        return false;
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    @Override
    public boolean isMoving() {
        return mc.player.movementInput.forwardKeyDown || mc.player.movementInput.backKeyDown || mc.player.movementInput.leftKeyDown || mc.player.movementInput.rightKeyDown;
    }
}
