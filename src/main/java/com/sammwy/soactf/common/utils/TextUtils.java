package com.sammwy.soactf.common.utils;

import net.minecraft.text.Text;

public class TextUtils {
    public static String asString(Object raw) {
        if (raw instanceof Text) {
            return ((Text) raw).getString();
        } else if (raw instanceof String) {
            return (String) raw;
        } else if (raw instanceof Object) {
            return raw.toString();
        } else {
            return String.valueOf(raw);
        }
    }

    public static Text from(Object raw) {
        String str = asString(raw);
        return Text.of(str.replace('&', '§'));
    }

    public static Text from(Object... args) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (Object arg : args) {
            if (first) {
                first = false;
            } else {
                builder.append(" ");
            }

            builder.append(asString(arg));
        }

        return from(builder.toString());
    }
}
