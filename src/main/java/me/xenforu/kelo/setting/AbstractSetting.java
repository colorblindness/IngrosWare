package me.xenforu.kelo.setting;

import com.esotericsoftware.reflectasm.FieldAccess;
import me.xenforu.kelo.traits.Labelable;

import java.lang.reflect.Field;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public abstract  class AbstractSetting<V> implements ISetting<V>, Labelable {
    private String label;
    private Object object;
    private final int valueIndex;
    private final FieldAccess access;

    public AbstractSetting(String label, Object object, Field field) {
        this.label = label;
        this.object = object;
        access = FieldAccess.get(object.getClass());
        valueIndex = access.getIndex(field.getName());
    }

    @Override
    public V getValue() {
        Object foundValue = null;

        try {
            foundValue = access.get(object, valueIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (V) foundValue;
    }

    @Override
    public void setValue(V value) {
        try {
            access.set(object, valueIndex, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getLabel() {
        return label;
    }
}
