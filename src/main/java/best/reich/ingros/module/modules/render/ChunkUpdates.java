package best.reich.ingros.module.modules.render;

import best.reich.ingros.events.other.ChunkLoadEvent;
import best.reich.ingros.events.render.Render3DEvent;
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import net.b0at.api.event.Subscribe;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.HashSet;
import java.util.Set;

@ModuleManifest(label = "ChunkUpdates", category = ModuleCategory.RENDER, color = 0xfffAEF00,hidden = true)
public class ChunkUpdates extends ToggleableModule {
    private Set<ChunkData> cachedLoadedChunks = new HashSet<>();

    @Subscribe
    public void onRenderWorld(Render3DEvent event) {
        cachedLoadedChunks.forEach(this::renderChunk);
    }

    @Subscribe
    public void onChunkLoad(ChunkLoadEvent event) {
        cachedLoadedChunks.add(new ChunkData(event.getX() * 16, event.getZ() * 16));
    }

    private void renderChunk(ChunkData chunk) {
        final double x = chunk.x - mc.getRenderManager().viewerPosX;
        final double y = -mc.getRenderManager().viewerPosY;
        final double z = chunk.z - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB chunkBB = new AxisAlignedBB(0d, 0d, 0d, 16d, 0, 16d).offset(x, y, z);
        RenderUtil.drawESPOutline(chunkBB,  255, 255, 0,255,2);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        cachedLoadedChunks.clear();
    }

    private static class ChunkData {

        private final double x, z;

        ChunkData(double x, double z) {
            this.x = x;
            this.z = z;
        }
    }
}
