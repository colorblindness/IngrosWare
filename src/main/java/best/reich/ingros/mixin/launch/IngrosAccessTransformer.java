package best.reich.ingros.mixin.launch;

import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
public class IngrosAccessTransformer extends AccessTransformer {

    public IngrosAccessTransformer() throws IOException {
        super("ingros_at.cfg");
    }

}
