package me.xenforu.kelo.setting.impl;

import me.xenforu.kelo.setting.AbstractSetting;

import java.lang.reflect.Field;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public class BooleanSetting extends AbstractSetting<Boolean> {

    public BooleanSetting(String label, Object object, Field field) {
        super(label, object, field);
    }

    @Override
    public void setValue(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on")) {
            setValue(true);
        } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("off")) {
            setValue(false);
        }
    }
}
