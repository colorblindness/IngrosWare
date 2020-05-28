package me.xenforu.kelo.module.type;

import best.reich.ingros.IngrosWare;
import com.google.gson.JsonObject;
import me.xenforu.kelo.module.IModule;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.setting.impl.ColorSetting;
import me.xenforu.kelo.setting.impl.StringSetting;
import me.xenforu.kelo.traits.Toggleable;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public class ToggleableModule implements IModule, Toggleable {
    private String label, suffix;
    private ModuleCategory category;
    private int color, bind;
    private boolean enabled, hidden = false;

    public ToggleableModule() {
        if (getClass().isAnnotationPresent(ModuleManifest.class)) {
            ModuleManifest moduleManifest = getClass().getAnnotation(ModuleManifest.class);
            this.label = moduleManifest.label();
            this.category = moduleManifest.category();
            this.color = moduleManifest.color();
            this.bind = moduleManifest.bind();
            this.hidden = moduleManifest.hidden();
        }
    }

    @Override
    public void init() {
        IngrosWare.INSTANCE.settingManager.scan(this);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String s) {
        if (s.length() > 0)
            this.suffix = s.toLowerCase().substring(0, 1).toUpperCase() + s.toLowerCase().substring(1);
        else this.suffix = s;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public int getBind() {
        return bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public ModuleCategory getCategory() {
        return category;
    }

    @Override
    public boolean getState() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            IngrosWare.INSTANCE.bus.registerListener(this);
            onEnable();
        } else {
            IngrosWare.INSTANCE.bus.deregisterListener(this);
            onDisable();
        }
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void toggle() {
        setEnabled(!getState());
    }

    @Override
    public boolean isEnabled() {
        return getState();
    }

    @Override
    public void save(JsonObject destination) {
        destination.addProperty("Enabled", isEnabled());
        destination.addProperty("Keybind", getBind());
        destination.addProperty("Color", getColor());
        destination.addProperty("Hidden", isHidden());
        if (IngrosWare.INSTANCE.settingManager.getSettingsFromObject(this) != null) {
            IngrosWare.INSTANCE.settingManager.getSettingsFromObject(this).forEach(property -> {
                if (property instanceof ColorSetting) {
                    final ColorSetting colorSetting = (ColorSetting) property;
                    destination.addProperty(property.getLabel(), colorSetting.getValue().getRGB());
                } else if (property instanceof StringSetting) {
                    final StringSetting stringSetting = (StringSetting) property;
                    final String escapedStr = StringEscapeUtils.escapeJava(stringSetting.getValue());
                    destination.addProperty(property.getLabel(), escapedStr);
                } else destination.addProperty(property.getLabel(), property.getValue().toString());
            });
        }
    }

    @Override
    public void load(JsonObject source) {
        if (source.has("Enabled") && source.get("Enabled").getAsBoolean()) {
            setEnabled(true);
        }
        if (source.has("Keybind")) {
            setBind(source.get("Keybind").getAsInt());
        }
        if (source.has("Color")) {
            setColor(source.get("Color").getAsInt());
        }
        if (source.has("Hidden") && source.get("Hidden").getAsBoolean()) {
            setHidden(true);
        }
        if (IngrosWare.INSTANCE.settingManager.getSettingsFromObject(this) != null) {
            source.entrySet().forEach(entry -> IngrosWare.INSTANCE.settingManager.getSetting(this, entry.getKey()).ifPresent(property -> {
                if (property instanceof ColorSetting) {
                    final ColorSetting colorSetting = (ColorSetting) property;
                    colorSetting.setValue(entry.getValue().getAsString());
                } else if (property instanceof StringSetting) {
                    final StringSetting stringSetting = (StringSetting) property;
                    stringSetting.setValue(StringEscapeUtils.unescapeJava(entry.getValue().getAsString()));
                } else property.setValue(entry.getValue().getAsString());
            }));
        }
    }
}
