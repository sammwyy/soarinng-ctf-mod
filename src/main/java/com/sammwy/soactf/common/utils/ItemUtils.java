package com.sammwy.soactf.common.utils;

import com.sammwy.soactf.server.chat.Color;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemUtils {
    public static String itemsAsDescriptor(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        return id.getNamespace() + ":" + id.getPath();
    }

    public static String itemsAsDescriptor(ItemStack stack) {
        String desc = itemsAsDescriptor(stack.getItem());
        int amount = stack.getCount();
        int damage = stack.getDamage();
        return desc + " amount=" + amount + " damage=" + damage;
    }

    public static Item getItem(String name) {
        String namespace = name.split(":")[0];
        String path = name.contains(":") ? name.split(":")[1] : null;

        if (namespace != null && path == null) {
            path = namespace;
            namespace = "minecraft";
        }

        Identifier identifier = new Identifier(namespace, path);
        return Registries.ITEM.get(identifier);
    }

    public static ItemStack getItemStack(String name) {
        Item item = null;
        GenericMap<String> props = new GenericMap<>();

        for (String part : name.split(" ")) {
            if (item == null) {
                item = getItem(part);
            } else {
                String prop = part.split("=")[0];
                String value = part.contains("=") ? part.split("=")[1] : null;
                props.put(prop, value != null ? value : true);
            }
        }

        ItemStack stack = new ItemStack(item, props.getInt("amount", 1));

        if (props.has("name")) {
            stack.setCustomName(Text.of(props.getString("name", "")));
        }

        if (props.has("damage")) {
            stack.setDamage(props.getInt("damage", 0));
        }

        return stack;
    }

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
