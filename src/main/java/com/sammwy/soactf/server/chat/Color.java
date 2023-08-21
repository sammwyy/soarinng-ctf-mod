package com.sammwy.soactf.server.chat;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;

public enum Color {
    GREEN("a", "55FF55", Blocks.LIME_BANNER, Items.LIME_BANNER),
    AQUA("b", "55FFFF", Blocks.LIGHT_BLUE_BANNER, Items.LIGHT_BLUE_BANNER),
    RED("c", "FF5555", Blocks.RED_BANNER, Items.RED_BANNER),
    LIGHT_PURPLE("d", "FF55FF", Blocks.MAGENTA_BANNER, Items.MAGENTA_BANNER),
    YELLOW("e", "FFFF55", Blocks.YELLOW_BANNER, Items.YELLOW_BANNER),
    WHITE("f", "FFFFFF", Blocks.WHITE_BANNER, Items.WHITE_BANNER),
    BLACK("0", "000000", Blocks.BLACK_BANNER, Items.BLACK_BANNER),
    DARK_BLUE("1", "0000AA", Blocks.BLUE_BANNER, Items.BLUE_BANNER),
    DARK_GREEN("2", "00AA00", Blocks.GREEN_BANNER, Items.GREEN_BANNER),
    DARK_AQUA("3", "00AAAA", Blocks.CYAN_BANNER, Items.CYAN_BANNER),
    DARK_RED("4", "AA0000", Blocks.RED_BANNER, Items.RED_BANNER),
    DARK_PURPLE("5", "AA00AA", Blocks.PURPLE_BANNER, Items.PURPLE_BANNER),
    GOLD("6", "FFAA00", Blocks.ORANGE_BANNER, Items.ORANGE_BANNER),
    GRAY("7", "AAAAAA", Blocks.LIGHT_GRAY_BANNER, Items.LIGHT_GRAY_BANNER),
    DARK_GRAY("8", "555555", Blocks.GRAY_BANNER, Items.GRAY_BANNER),
    BLUE("9", "5555FF", Blocks.BLUE_BANNER, Items.BLUE_BANNER);

    private String charCode;
    private String hex;
    private int decimal;
    private Block block;
    private Item item;

    private Color(String charCode, String hex, Block block, Item item) {
        this.charCode = charCode;
        this.hex = hex;
        this.decimal = Integer.parseInt(hex, 16);
        this.block = block;
        this.item = item;
    }

    public Block getBlock() {
        return this.block;
    }

    public String getChatColor() {
        return "\u00A7" + this.charCode;
    }

    public Formatting getChatFormat() {
        return Formatting.byCode(this.charCode.charAt(0));
    }

    public int getDecimalColor() {
        return this.decimal;
    }

    public String getHexColor() {
        return this.hex;
    }

    public Item getItem() {
        return this.item;
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
