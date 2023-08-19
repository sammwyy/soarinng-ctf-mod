package com.sammwy.soactf.common.utils;

import net.minecraft.text.Text;

public class TextUtils {
    public static Text from(String raw) {
        return Text.of(raw.replace('&', '§'));
    }
}
