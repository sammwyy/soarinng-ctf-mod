package com.sammwy.soactf.server.chat;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;

public enum Color {
    GREEN("a", DyeColor.GREEN, Blocks.LIME_BANNER, Items.LIME_BANNER),
    AQUA("b", DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_BANNER, Items.LIGHT_BLUE_BANNER),
    RED("c", DyeColor.RED, Blocks.RED_BANNER, Items.RED_BANNER),
    LIGHT_PURPLE("d", DyeColor.MAGENTA, Blocks.MAGENTA_BANNER, Items.MAGENTA_BANNER),
    YELLOW("e", DyeColor.YELLOW, Blocks.YELLOW_BANNER, Items.YELLOW_BANNER),
    WHITE("f", DyeColor.WHITE, Blocks.WHITE_BANNER, Items.WHITE_BANNER),
    BLACK("0", DyeColor.BLACK, Blocks.BLACK_BANNER, Items.BLACK_BANNER),
    DARK_BLUE("1", DyeColor.BLUE, Blocks.BLUE_BANNER, Items.BLUE_BANNER),
    DARK_GREEN("2", DyeColor.GREEN, Blocks.GREEN_BANNER, Items.GREEN_BANNER),
    DARK_AQUA("3", DyeColor.CYAN, Blocks.CYAN_BANNER, Items.CYAN_BANNER),
    DARK_RED("4", DyeColor.RED, Blocks.RED_BANNER, Items.RED_BANNER),
    DARK_PURPLE("5", DyeColor.PURPLE, Blocks.PURPLE_BANNER, Items.PURPLE_BANNER),
    GOLD("6", DyeColor.ORANGE, Blocks.ORANGE_BANNER, Items.ORANGE_BANNER),
    GRAY("7", DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_BANNER, Items.LIGHT_GRAY_BANNER),
    DARK_GRAY("8", DyeColor.GRAY, Blocks.GRAY_BANNER, Items.GRAY_BANNER),
    BLUE("9", DyeColor.BLUE, Blocks.BLUE_BANNER, Items.BLUE_BANNER);

    private String charCode;
    private DyeColor dyeColor;
    private Block block;
    private Item item;

    private Color(String charCode, DyeColor dyeColor, Block block, Item item) {
        this.charCode = charCode;
        this.dyeColor = dyeColor;
        this.block = block;
        this.item = item;
    }

    public String getChatColor() {
        return "\u00A7" + this.charCode;
    }

    public Formatting getChatFormat() {
        return Formatting.byCode(this.charCode.charAt(0));
    }

    public Block getBlock() {
        return this.block;
    }

    public Item getItem() {
        return this.item;
    }

    public DyeColor getDyeColor() {
        return this.dyeColor;
    }

    public static Color get(String colorName) {
        for (Color color : values()) {
            if (color.name().equalsIgnoreCase(colorName)) {
                return color;
            }
        }

        return null;
    }
}
