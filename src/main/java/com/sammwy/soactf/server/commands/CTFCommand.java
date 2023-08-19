package com.sammwy.soactf.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import com.sammwy.soactf.server.SoaCTFServer;
import com.sammwy.soactf.server.chat.ChatColor;
import com.sammwy.soactf.server.commands.types.GamePhaseType;
import com.sammwy.soactf.server.game.GamePhase;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public final class CTFCommand {
    private final SoaCTFServer server;

    public CTFCommand(SoaCTFServer server) {
        this.server = server;
    }

    private int handleStart(ServerCommandSource source) {
        boolean success = this.server.getGame().startRound();
        if (success) {
            source.sendFeedback(Text.of(ChatColor.GREEN + "La ronda ha sido iniciada."), false);
        } else {
            source.sendFeedback(Text.of(ChatColor.RED + "La ronda ya se encuentra iniciada."), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    private int handleStop(ServerCommandSource source) {
        boolean success = this.server.getGame().stopRound();

        if (success) {
            source.sendFeedback(Text.of(ChatColor.GREEN + "La ronda ha sido detenida."), false);
        } else {
            source.sendFeedback(Text.of(ChatColor.RED + "La ronda ya se encuentra detenida."), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    private int handleSetPhase(ServerCommandSource source, GamePhase phase) {
        this.server.getGame().setPhase(phase);
        source.sendFeedback(Text.of(ChatColor.GREEN + "La fase ha sido cambiada a " + phase.name() + "."), false);
        return Command.SINGLE_SUCCESS;
    }

    private int handleSetTime(ServerCommandSource source, int time) {
        this.server.getGame().setTime(time);
        source.sendFeedback(Text.of(ChatColor.GREEN + "El tiempo ha sido cambiado a " + time + "."), false);
        return Command.SINGLE_SUCCESS;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("ctf").requires(source -> source.hasPermissionLevel(4))
                        .then(literal("start").executes(ctx -> handleStart(ctx.getSource())))
                        .then(literal("stop").executes(ctx -> handleStop(ctx.getSource())))
                        .then(literal("setphase")
                                .then(argument("phase", GamePhaseType.gamePhase())
                                        .executes(ctx -> handleSetPhase(ctx.getSource(),
                                                GamePhaseType.getGamePhase("phase", ctx)))))
                        .then(literal("settime")
                                .then(argument("time", IntegerArgumentType.integer(0))
                                        .executes(ctx -> handleSetTime(ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "time")))))
                        .then(literal("setphase")
                                .then(argument("phase", GamePhaseType.gamePhase())
                                        .executes(ctx -> handleSetPhase(ctx.getSource(),
                                                GamePhaseType.getGamePhase("phase", ctx))))));
    }
}
