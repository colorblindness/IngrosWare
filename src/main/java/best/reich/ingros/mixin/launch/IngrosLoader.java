package best.reich.ingros.mixin.launch;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.tools.obfuscation.mcp.ObfuscationServiceMCP;

import java.util.List;
import java.util.Map;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
@IFMLLoadingPlugin.Name("IngrosWare")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class IngrosLoader implements IFMLLoadingPlugin {

    public IngrosLoader() {
        MixinBootstrap.init();

        String obfuscation = ObfuscationServiceMCP.NOTCH;
        for (String s : (List<String>) Launch.blackboard.get("TweakClasses")) {
            if (s.contains("net.minecraftforge.fml.common.launcher")) {
                obfuscation = ObfuscationServiceMCP.SEARGE;
                break;
            }
        }

        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext(obfuscation);

        Mixins.addConfiguration("mixins.ingros.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) { }

    @Override
    public String getAccessTransformerClass() {
        return IngrosAccessTransformer.class.getName();
    }

}
