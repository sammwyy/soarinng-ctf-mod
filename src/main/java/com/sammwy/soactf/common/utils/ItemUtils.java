package com.sammwy.soactf.common.utils;

import com.sammwy.soactf.server.chat.Color;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class ItemUtils {
    public static ItemStack colorizeItem(ItemStack item, Color color) {
        NbtCompound nbt = item.getOrCreateNbt();
        NbtCompound display = nbt.getCompound("display");
        display.putInt(ItemStack.COLOR_KEY, color.getDecimalColor());
        nbt.put("display", display);
        return item;
    }

    public static ItemStack createColorizedItem(Item type, Color color) {
        return colorizeItem(new ItemStack(type, 1), color);
    }
}
