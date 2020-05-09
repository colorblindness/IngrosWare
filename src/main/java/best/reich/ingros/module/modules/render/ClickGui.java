package best.reich.ingros.module.modules.render;

import best.reich.ingros.gui.clickgui.ClickGUI;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Setting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@ModuleManifest(label = "ClickGui", category = ModuleCategory.RENDER, color = 0xFF05BAEC,bind = Keyboard.KEY_DELETE, hidden = true)
public class ClickGui extends ToggleableModule {
    @Setting("Color")
    public Color color = new Color(179,0,46);

    private ClickGUI clickGUI;
    @Override
    public void onEnable() {
        super.onEnable();
        if (clickGUI == null) {
            clickGUI = new ClickGUI();
            clickGUI.init();
        }
        mc.displayGuiScreen(clickGUI);
        toggle();
    }
}
