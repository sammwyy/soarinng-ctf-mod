package com.sammwy.soactf.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sammwy.soactf.common.utils.TextUtils;
import com.sammwy.soactf.server.SoaCTFServer;
import com.sammwy.soactf.server.chat.Color;
import com.sammwy.soactf.server.commands.types.ColorType;
import com.sammwy.soactf.server.commands.types.PlayerType;
import com.sammwy.soactf.server.commands.types.TeamType;
import com.sammwy.soactf.server.flags.Flag;
import com.sammwy.soactf.server.players.Player;
import com.sammwy.soactf.server.teams.CTFTeam;
import com.sammwy.soactf.server.teams.CTFTeamManager;
import com.sammwy.soactf.server.world.BlockPosition;
import com.sammwy.soactf.server.world.Position;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;

public final class CTFTeamCommand {
    private final SoaCTFServer server;
    private final String PREFIX = "&d&lCTF &8\u00BB&7";

    public CTFTeamCommand(SoaCTFServer server) {
        this.server = server;
    }

    private int handleCreateTeam(ServerCommandSource source, String name, Color color) {
        CTFTeamManager teams = this.server.getTeamManager();

        String id = name.toLowerCase().replace(" ", "_");
        CTFTeam team = teams.getTeam(id);

        if (team != null) {
            source.sendFeedback(TextUtils.from(PREFIX, "El equipo &c" + name + " &7ya existe."), false);
            return Command.SINGLE_SUCCESS;
        }

        team = this.server.getTeamManager().createTeam(name, color);
        source.sendFeedback(TextUtils.from(PREFIX, "El equipo " + team.getDisplayName() + " &7ha sido creado."),
                false);
        return Command.SINGLE_SUCCESS;
    }

    private int handleDeleteTeam(ServerCommandSource source, CTFTeam team) {
        this.server.getTeamManager().deleteTeam(team);
        source.sendFeedback(
                TextUtils.from(PREFIX, "Equipo " + team.getDisplayName() + " &7ha sido &celiminado&7."),
                false);
        return Command.SINGLE_SUCCESS;
    }

    private int handleSetFlag(ServerCommandSource source, CTFTeam team) {
        ServerPlayerEntity entity = source.getPlayer();
        Player player = this.server.getPlayerManager().getPlayer(entity);
        BlockPosition position = new BlockPosition(player.getPosition());
        team.setFlagSpawn(position);
        source.sendFeedback(TextUtils.from(PREFIX + "La bandera del equipo " + team.getDisplayName()
                + " &7ha sido colocada en &a" + position.toString() + "&7."), false);
        return Command.SINGLE_SUCCESS;
    }

    private int handleJoin(ServerCommandSource source, CTFTeam team, Player player) {
        player.setTeam(team);
        this.server.getGame().joinPlayer(player);
        source.sendFeedback(
                TextUtils.from(PREFIX + "El jugador &a" + player.getName() + " &7se ha unido al equipo "
                        + team.getDisplayName() + "&7."),
                false);
        return Command.SINGLE_SUCCESS;
    }

    private int handleList(ServerCommandSource source) {
        Collection<CTFTeam> teams = this.server.getTeamManager().getTeams();

        if (teams.size() == 0) {
            source.sendFeedback(TextUtils.from(PREFIX + "&cError: &7No hay equipos."), false);
            return Command.SINGLE_SUCCESS;
        }

        source.sendFeedback(TextUtils.from(PREFIX, "Equipos:"), false);

        for (CTFTeam team : teams) {
            source.sendFeedback(
                    TextUtils.from("&8- " + team.getDisplayName() + " &7(players="
                            + team.getPlayers().size()
                            + " | points=" + team.getPoints() + " | alive=" + team.isAlive()
                            + " | flag=" + team.getFlag().getState().name() + ")"),
                    false);
        }

        return Command.SINGLE_SUCCESS;
    }

    private int handlePlayers(ServerCommandSource source, CTFTeam team) {
        Collection<Player> players = team.getPlayers();

        if (players.size() == 0) {
            source.sendFeedback(TextUtils.from(PREFIX + "&cError: &7No hay jugadores en el equipo."),
                    false);
            return Command.SINGLE_SUCCESS;
        }

        source.sendFeedback(TextUtils.from(PREFIX, "Jugadores en el equipo " + team.getDisplayName() + ":"),
                false);

        for (Player player : players) {
            String capturedStr = "none";
            Flag captured = player.getCapturedFlag();

            if (captured != null) {
                capturedStr = captured.getTeam().getDisplayName();
            }

            source.sendFeedback(
                    TextUtils.from("&8- &a" + player.getName() + "&7(state="
                            + player.getState().name() + " | captured="
                            + capturedStr + " | captures=" + player.getCaptures() + ")"),
                    false);
        }

        return Command.SINGLE_SUCCESS;
    }

    private int handleSetSpawn(ServerCommandSource source, CTFTeam team) {
        ServerPlayerEntity entity = source.getPlayer();
        Player player = this.server.getPlayerManager().getPlayer(entity);
        Position position = new Position(player.getPosition());
        team.setSpawn(position);
        source.sendFeedback(TextUtils.from(PREFIX + "El spawn del equipo " + team.getDisplayName()
                + " &7ha sido colocado en &a" + position.toString() + "&7."), false);
        return Command.SINGLE_SUCCESS;
    }

    private int handleKill(ServerCommandSource source, CTFTeam team) {
        this.server.getGame().killTeam(team);
        source.sendFeedback(TextUtils.from(PREFIX + "El equipo " + team.getDisplayName()
                + " &7ha sido eliminado."), false);
        return Command.SINGLE_SUCCESS;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("ctfteam").requires(source -> source.hasPermissionLevel(4))
                        .then(literal("create")
                                .then(argument("name", StringArgumentType.word())
                                        .then(argument("color",
                                                ColorType.color())
                                                .suggests(ColorType
                                                        .color()::listSuggestions)
                                                .executes(ctx -> handleCreateTeam(
                                                        ctx.getSource(),
                                                        StringArgumentType
                                                                .getString(ctx, "name"),
                                                        ColorType.getColor(
                                                                "color",
                                                                ctx))))))

                        .then(literal("delete")
                                .then(argument("team", TeamType.team())
                                        .suggests(TeamType
                                                .team()::listSuggestions)
                                        .executes(ctx -> handleDeleteTeam(
                                                ctx.getSource(),
                                                TeamType.getTeam("team",
                                                        ctx)))))

                        .then(literal("flag")
                                .then(argument("team", TeamType.team())
                                        .suggests(TeamType
                                                .team()::listSuggestions)
                                        .requires(source -> source
                                                .isExecutedByPlayer())
                                        .executes(ctx -> handleSetFlag(
                                                ctx.getSource(),
                                                TeamType.getTeam("team",
                                                        ctx)))))

                        .then(literal("join")
                                .then(argument("team", TeamType.team())
                                        .suggests(TeamType
                                                .team()::listSuggestions)
                                        .then(argument("player",
                                                PlayerType.player())
                                                .suggests(PlayerType
                                                        .player()::listSuggestions)
                                                .executes(ctx -> handleJoin(
                                                        ctx.getSource(),
                                                        TeamType.getTeam(
                                                                "team",
                                                                ctx),
                                                        PlayerType.getPlayer(
                                                                "player",
                                                                ctx))))))

                        .then(literal("kill")
                                .then(argument("team", TeamType.team())
                                        .suggests(TeamType
                                                .team()::listSuggestions)
                                        .executes(ctx -> handleKill(
                                                ctx.getSource(),
                                                TeamType.getTeam("team",
                                                        ctx)))))

                        .then(literal("list")
                                .executes(ctx -> handleList(ctx.getSource())))

                        .then(literal("players")
                                .then(argument("team", TeamType.team())
                                        .suggests(TeamType
                                                .team()::listSuggestions)
                                        .executes(ctx -> handlePlayers(
                                                ctx.getSource(),
                                                TeamType.getTeam("team",
                                                        ctx)))))

                        .then(literal("spawn")
                                .requires(source -> source.isExecutedByPlayer())
                                .then(argument("team", TeamType.team())
                                        .suggests(TeamType
                                                .team()::listSuggestions)
                                        .executes(ctx -> handleSetSpawn(
                                                ctx.getSource(),
                                                TeamType.getTeam("team",
                                                        ctx))))));
    }
}
