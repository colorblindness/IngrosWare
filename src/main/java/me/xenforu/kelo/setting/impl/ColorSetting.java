package me.xenforu.kelo.setting.impl;

import me.xenforu.kelo.setting.AbstractSetting;

import java.awt.*;
import java.lang.reflect.Field;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public class ColorSetting extends AbstractSetting<Color> {

    public ColorSetting(String label, Object object, Field field) {
        super(label, object, field);
    }

    @Override
    public void setValue(String value) {

    }
}
