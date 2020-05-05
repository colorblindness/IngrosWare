package me.xenforu.kelo.module.type;

import best.reich.ingros.IngrosWare;
import com.google.gson.JsonObject;
import me.xenforu.kelo.module.IModule;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.setting.impl.ColorSetting;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public class PersistentModule implements IModule {
    private String label;
    private ModuleCategory category;

    public PersistentModule() {
        if (getClass().isAnnotationPresent(ModuleManifest.class)) {
            ModuleManifest moduleManifest = getClass().getAnnotation(ModuleManifest.class);
            this.label = moduleManifest.label();
            this.category = moduleManifest.category();
        }
    }

    @Override
    public void init() {
        IngrosWare.INSTANCE.bus.registerListener(this);
        IngrosWare.INSTANCE.settingManager.scan(this);
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
    public boolean isEnabled() {
        return mc.player != null;
    }

    @Override
    public void save(JsonObject destination) {
        if (IngrosWare.INSTANCE.settingManager.getSettingsFromObject(this) != null) {
            IngrosWare.INSTANCE.settingManager.getSettingsFromObject(this).forEach(property -> {
                if (property instanceof ColorSetting) {
                    final ColorSetting colorSetting = (ColorSetting) property;
                    destination.addProperty(property.getLabel(), colorSetting.getValue().getRGB());
                } else
                    destination.addProperty(property.getLabel(), property.getValue().toString());
            });
        }
    }

    @Override
    public void load(JsonObject source) {
        if (IngrosWare.INSTANCE.settingManager.getSettingsFromObject(this) != null) {
            source.entrySet().forEach(entry -> IngrosWare.INSTANCE.settingManager.getSetting(this, entry.getKey()).ifPresent(property -> {
                if (property instanceof ColorSetting) {
                    final ColorSetting colorSetting = (ColorSetting) property;
                    colorSetting.setValue(entry.getValue().getAsString());
                } else property.setValue(entry.getValue().getAsString());
            }));
        }
    }
}
