package com.sammwy.soactf.common.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;

public class ItemUtils {
    public static ItemStack colorizeItem(ItemStack item, DyeColor color) {
        NbtCompound nbt = item.getOrCreateNbt();
        nbt.putInt(ItemStack.COLOR_KEY, color.getId());
        item.setNbt(nbt);
        return item;
    }

    public static ItemStack createColorizedItem(Item type, DyeColor color) {
        return colorizeItem(new ItemStack(type, 1), color);
    }
}
