package me.xenforu.kelo.util.parse.impl;

import me.xenforu.kelo.util.parse.Parser;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public class StringParser extends Parser<String> {
    private boolean quoted;

    @Override
    public String parse(String input) {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        char[] charArray;
        for (int length = (charArray = input.toCharArray()).length, i = 0; i < length; ++i) {
            final char character = charArray[i];
            ++index;
            boolean cancel = false;
            switch (character) {
                case '\"': {
                    this.quoted = !this.quoted;
                    break;
                }
                case ' ': {
                    if (this.quoted) {
                        builder.append(character);
                        break;
                    }
                    cancel = true;
                    break;
                }
                default: {
                    builder.append(character);
                    break;
                }
            }
            if (cancel) {
                break;
            }
        }
        this.setIndex(index);
        return builder.toString();
    }

    @Override
    public boolean canHandleType(Class type) {
        return String.class.isAssignableFrom(type);
    }
}