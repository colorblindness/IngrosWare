package me.xenforu.kelo.setting;

import com.esotericsoftware.reflectasm.FieldAccess;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Mode;
import me.xenforu.kelo.setting.annotation.Setting;
import me.xenforu.kelo.setting.impl.*;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.*;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public class SettingManager {

    private Map<Object, List<AbstractSetting>> settings = new HashMap<>();

    public void register(Object object, AbstractSetting setting) {
        settings.computeIfAbsent(object, collection -> new ArrayList<>());
        settings.get(object).add(setting);
    }

    public void scan(Object object) {
        FieldAccess access = FieldAccess.get(object.getClass());
        for (Field field : object.getClass().getDeclaredFields()) {
            boolean accessibility = field.isAccessible();
            if (field.isAnnotationPresent(Setting.class)) {
                field.setAccessible(true);
                Setting setting = field.getAnnotation(Setting.class);
                try {
                    Object val = access.get(object, field.getName());
                    if (val instanceof Boolean) {
                        register(object, new BooleanSetting(setting.value(), object, field));
                    }

                    if (val instanceof String) {
                        if (field.isAnnotationPresent(Mode.class)) {
                            Mode mode = field.getAnnotation(Mode.class);
                            register(object, new ModeStringSetting(setting.value(), object, field, mode.value()));
                        } else {
                            register(object, new StringSetting(setting.value(), object, field));
                        }
                    }

                    if (val instanceof Color) {
                        register(object, new ColorSetting(setting.value(), object, field));
                    }

                    if (field.isAnnotationPresent(Clamp.class)) {
                        Clamp clamp = field.getAnnotation(Clamp.class);

                        /* We have to do this to determine the number property's type. */
                        if (val instanceof Integer) {
                            register(object, new NumberSetting(setting.value(), object, field, Integer.parseInt(clamp.minimum()), Integer.parseInt(clamp.maximum()), Integer.parseInt(clamp.inc())));
                        } else if (val instanceof Double) {
                            register(object, new NumberSetting<>(setting.value(), object, field, Double.parseDouble(clamp.minimum()), Double.parseDouble(clamp.maximum()), Integer.parseInt(clamp.inc())));
                        } else if (val instanceof Float) {
                            register(object, new NumberSetting<>(setting.value(), object, field, Float.parseFloat(clamp.minimum()), Float.parseFloat(clamp.maximum()), Integer.parseInt(clamp.inc())));
                        } else if (val instanceof Long) {
                            register(object, new NumberSetting<>(setting.value(), object, field, Long.parseLong(clamp.minimum()), Long.parseLong(clamp.maximum()), Integer.parseInt(clamp.inc())));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                field.setAccessible(accessibility);
            }
        }
    }

    public Optional<AbstractSetting> getSetting(Object object, String label) {
        Optional<AbstractSetting> found = Optional.empty();
        for (AbstractSetting property : getSettingsFromObject(object)) {
            if (property.getLabel().equalsIgnoreCase(label)) {
                found = Optional.of(property);
                break;
            }
        }
        return found;
    }

    public List<AbstractSetting> getSettingsFromObject(Object object) {
        if (settings.get(object) != null) {
            return settings.get(object);
        } else {
            return null;
        }
    }
}
