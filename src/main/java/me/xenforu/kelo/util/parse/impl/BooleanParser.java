package me.xenforu.kelo.util.parse.impl;

import me.xenforu.kelo.util.parse.Parser;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public class BooleanParser extends Parser<Boolean> {
    @Override
    public Boolean parse(String input) {
        if (input.equalsIgnoreCase("true")) {
            setIndex(4);
            return true;
        } else if (input.equalsIgnoreCase("false")) {
            setIndex(5);
            return false;
        }
        return null;
    }

    @Override
    public boolean canHandleType(Class type) {
        return Boolean.class.isAssignableFrom(type) || Boolean.TYPE.isAssignableFrom(type);
    }
}