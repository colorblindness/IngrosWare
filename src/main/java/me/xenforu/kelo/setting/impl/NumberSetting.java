package me.xenforu.kelo.setting.impl;

import me.xenforu.kelo.setting.AbstractSetting;
import me.xenforu.kelo.util.math.MathUtil;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Field;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public class NumberSetting<T extends Number> extends AbstractSetting<T> {
    private T minimum;
    private T maximum;
    private T inc;
    private final Class cls;
    public NumberSetting(String label, Object object, Field field, T minimum, T maximum, T inc) {
        super(label, object, field);
        this.minimum = minimum;
        this.maximum = maximum;
        this.inc = inc;
        cls = field.getType();
    }


    @Override
    public void setValue(T value) {
        super.setValue(MathUtil.clamp(value, minimum, maximum));
    }

    @Override
    public void setValue(String value) {
        if (cls == Integer.class || cls == Integer.TYPE) {
            setValue((T) NumberUtils.createInteger(value));
        } else if (cls == Double.class || cls == Double.TYPE) {
            setValue((T) NumberUtils.createDouble(value));
        } else if (cls == Float.class || cls == Float.TYPE) {
            setValue((T) NumberUtils.createFloat(value));
        } else if (cls == Long.class || cls == Long.TYPE) {
            setValue((T) NumberUtils.createLong(value));
        }
    }
    public T getMinimum() {
        return minimum;
    }

    public void setMinimum(T minimum) {
        this.minimum = minimum;
    }

    public T getMaximum() {
        return maximum;
    }

    public void setMaximum(T maximum) {
        this.maximum = maximum;
    }


}
