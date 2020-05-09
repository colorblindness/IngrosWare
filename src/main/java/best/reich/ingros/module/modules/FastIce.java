package best.reich.ingros.module.modules;

import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Setting;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
@ModuleManifest(label = "FastIce", category = ModuleCategory.MOVEMENT, color = 0x1FDEDB)
public class FastIce extends ToggleableModule {
    @Clamp(minimum = "0.1", maximum = "1.0")
    @Setting("Speed")
    public float speed = 0.6f;

    private final Block[] ICES = new Block[] { Blocks.ICE, Blocks.PACKED_ICE, Blocks.FROSTED_ICE };

    @Override
    public void onEnable() {
        final float slipperiness = Math.abs(speed - 1.0f);
        setIces(slipperiness);
    }

    @Override
    public void onDisable() {
        setIces(0.98f);
    }

    private void setIces(final float slipperiness) {
        for (final Block ice : ICES) {
            ice.setDefaultSlipperiness(slipperiness);
        }
    }
}
