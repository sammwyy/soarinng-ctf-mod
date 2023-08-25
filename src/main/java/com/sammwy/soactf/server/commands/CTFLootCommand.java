package com.sammwy.soactf.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import com.sammwy.soactf.common.utils.TextUtils;
import com.sammwy.soactf.server.SoaCTFServer;
import com.sammwy.soactf.server.loots.LootBox;
import com.sammwy.soactf.server.players.Player;
import com.sammwy.soactf.server.world.BlockPosition;

import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

import java.util.List;

public class CTFLootCommand {
    private final SoaCTFServer server;
    private final String PREFIX = "&d&lCTF &8\u00BB&7";

    public CTFLootCommand(SoaCTFServer server) {
        this.server = server;
    }

    public int handleAdd(ServerCommandSource source) {
        Player player = this.server.getPlayerManager().getPlayer(source.getPlayer());
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.isEmpty()) {
            source.sendError(Text.literal("No item in your hand."));
        } else {
            this.server.getLootBoxManager().addItem(item);
            source.sendFeedback(TextUtils.from(PREFIX + "Added item in your hand to the loot boxes config."), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    public int handleCreate(ServerCommandSource source) {
        Player player = this.server.getPlayerManager().getPlayer(source.getPlayer());
        BlockPosition pos = player.getBlockPosition();
        this.server.getLootBoxManager().create(pos);
        source.sendFeedback(TextUtils.from(PREFIX, "Created loot box at your current position."), false);
        return Command.SINGLE_SUCCESS;
    }

    public int handleItems(ServerCommandSource source) {
        List<String> items = this.server.getConfig().loots.items;
        source.sendFeedback(TextUtils.from(PREFIX, "Loot items:"), false);

        for (String str : items) {
            source.sendFeedback(TextUtils.from("&8-&a", str), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    public int handleList(ServerCommandSource source) {
        List<LootBox> boxes = this.server.getLootBoxManager().getBoxes();
        source.sendFeedback(TextUtils.from(PREFIX, "Loot Boxes:"), false);

        for (LootBox box : boxes) {
            source.sendFeedback(box.toText("&8-"), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("ctfloot").requires(source -> source.hasPermissionLevel(4))
                        .then(literal("add")
                                .requires(source -> source.isExecutedByPlayer())
                                .executes(ctx -> {
                                    return handleAdd(ctx.getSource());
                                }))

                        .then(literal("create")
                                .requires(source -> source.isExecutedByPlayer())
                                .executes(ctx -> {
                                    return handleCreate(ctx.getSource());
                                }))

                        .then(literal("items")
                                .executes(ctx -> {
                                    return handleItems(ctx.getSource());
                                }))

                        .then(literal("list")
                                .executes(ctx -> {
                                    return handleList(ctx.getSource());
                                }))

        );
    }
}
