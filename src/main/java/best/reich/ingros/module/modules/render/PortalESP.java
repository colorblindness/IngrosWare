package best.reich.ingros.module.modules.render;

import best.reich.ingros.events.other.TickEvent;
import best.reich.ingros.events.render.Render3DEvent;
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

@ModuleManifest(label = "PortalESP", category = ModuleCategory.RENDER, color = 0xffAEAE33, hidden = true)
public class PortalESP extends ToggleableModule {
    private int cooldownTicks;
    private final ArrayList<BlockPos> blockPosArrayList = new ArrayList<>();

    @Subscribe
    public void onTick(TickEvent e) {
        if (mc.world == null) {
            return;
        }
        if (cooldownTicks < 1) {
            blockPosArrayList.clear();
            compileDL();
            cooldownTicks = 80;
        }
        --cooldownTicks;
    }

    @Subscribe
    public void onRender3D(Render3DEvent event) {
        if (this.mc.world == null) {
            return;
        }
        for (final BlockPos pos : blockPosArrayList) {
            final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - mc.getRenderManager().viewerPosX, pos.getY() - mc.getRenderManager().viewerPosY, pos.getZ() - mc.getRenderManager().viewerPosZ, pos.getX() + 1 - mc.getRenderManager().viewerPosX, pos.getY() + 1 - mc.getRenderManager().viewerPosY, pos.getZ() + 1 - mc.getRenderManager().viewerPosZ);
            if (RenderUtil.isInViewFrustrum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ, bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ))) {
                RenderUtil.drawESP(bb, 204, 0, 153, 40F);
                RenderUtil.drawESPOutline(bb, 204, 0, 153, 255f, 1f);
            }
        }
    }


    private void compileDL() {
        if (mc.world == null || mc.player == null) {
            return;
        }
        for (int x = (int) mc.player.posX - 60; x <= (int) mc.player.posX + 60; ++x) {
            for (int y = (int) mc.player.posZ - 60; y <= (int) mc.player.posZ + 60; ++y) {
                for (int z = (int) Math.max(mc.player.posY - 60, 0.0f); z <= Math.min(mc.player.posY + 60, 255); ++z) {
                    final BlockPos pos = new BlockPos(x, y, z);
                    final Block block = mc.world.getBlockState(pos).getBlock();
                    if (block == Blocks.PORTAL) {
                        blockPosArrayList.add(pos);
                    }
                }
            }
        }
    }
}
