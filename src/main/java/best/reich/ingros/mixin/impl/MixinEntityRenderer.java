package best.reich.ingros.mixin.impl;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.other.TraceEntityEvent;
import best.reich.ingros.events.render.HurtcamEvent;
import best.reich.ingros.events.render.Render2DEvent;
import best.reich.ingros.events.render.Render3DEvent;
import best.reich.ingros.mixin.accessors.IEntityRenderer;
import best.reich.ingros.util.render.GLUProjection;
import com.google.common.base.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;


@Mixin(EntityRenderer.class)
@Implements(@Interface(iface = IEntityRenderer.class, prefix = "ext$"))
public abstract class MixinEntityRenderer {

    @Shadow
    private void orientCamera(float partialTicks) {}

    @Intrinsic(displace = true)
    public void ext$cameraOrientation(float partialTicks) {
        orientCamera(partialTicks);
    }

    @Redirect(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiIngame.renderGameOverlay(F)V"))
    private void updateCameraAndRender$renderGameOverlay(GuiIngame guiIngame, float partialTicks) {
        guiIngame.renderGameOverlay(partialTicks);
        IngrosWare.INSTANCE.bus.fireEvent(new Render2DEvent(partialTicks,new ScaledResolution(Minecraft.getMinecraft())));
    }
    @Inject(
            method = "hurtCameraEffect",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hurtCameraEffect(float partialTicks, CallbackInfo ci) {
        final HurtcamEvent event = new HurtcamEvent();
        IngrosWare.INSTANCE.bus.fireEvent(event);
        if (event.isCancelled())
            ci.cancel();
    }
    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE_STRING", target = "net/minecraft/profiler/Profiler.endStartSection(Ljava/lang/String;)V", args = {"ldc=hand"}))
    private void onStartHand(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        final GLUProjection projection = GLUProjection.getInstance();
        final IntBuffer viewPort = GLAllocation.createDirectIntBuffer(16);
        final FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
        final FloatBuffer projectionPort = GLAllocation.createDirectFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionPort);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewPort);
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        projection.updateMatrices(viewPort, modelView, projectionPort, scaledResolution.getScaledWidth() / (double) Minecraft.getMinecraft().displayWidth,
                scaledResolution.getScaledHeight() / (double) Minecraft.getMinecraft().displayHeight);
        IngrosWare.INSTANCE.bus.fireEvent(new Render3DEvent(partialTicks));
    }

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        final TraceEntityEvent traceEntityEvent = new TraceEntityEvent();
        IngrosWare.INSTANCE.bus.fireEvent(traceEntityEvent);
        if (traceEntityEvent.isCancelled())
            return new ArrayList<>();
        else
            return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }
}
