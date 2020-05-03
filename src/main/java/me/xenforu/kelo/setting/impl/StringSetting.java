package me.xenforu.kelo.setting.impl;

import me.xenforu.kelo.setting.AbstractSetting;

import java.lang.reflect.Field;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public class StringSetting extends AbstractSetting<String> {

    public StringSetting(String label, Object object, Field field) {
        super(label, object, field);
    }
}
