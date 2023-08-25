package com.sammwy.soactf.server.game;

import com.sammwy.soactf.server.SoaCTFServer;
import com.sammwy.soactf.server.config.impl.CTFArenaConfig;
import com.sammwy.soactf.server.config.impl.CTFConfiguration;
import com.sammwy.soactf.server.config.impl.CTFMessagesConfig;
import com.sammwy.soactf.server.flags.Flag;
import com.sammwy.soactf.server.flags.FlagCaptureResult;
import com.sammwy.soactf.server.players.Player;
import com.sammwy.soactf.server.players.PlayerState;
import com.sammwy.soactf.server.teams.CTFTeam;

import net.minecraft.util.math.BlockPos;

public class Game {
    private SoaCTFServer server;
    private CTFArenaConfig arena;
    private CTFConfiguration config;
    private CTFMessagesConfig messages;

    private int currentTime;
    private GamePhase phase;

    public Game(SoaCTFServer server) {
        this.server = server;
        this.arena = server.getArenaConfig();
        this.config = server.getConfig();
        this.messages = server.getMessages();

        this.currentTime = 0;
        this.phase = GamePhase.WAITING;
    }

    public void broadcastActionBar(String message) {
        for (Player player : this.server.getPlayerManager().getPlayers()) {
            player.sendActionBar(message);
        }
    }

    public void broadcastMessage(String message) {
        for (Player player : this.server.getPlayerManager().getPlayers()) {
            player.sendMessage(message);
        }
    }

    public void captureFlag(Player player, Flag flag) {
        CTFTeam team = flag.getTeam();
        FlagCaptureResult result = flag.capture(player);

        String teamDisplay = team.getColor().getChatColor() + team.getName();
        String playerDisplay = player.getDisplayName();

        if (result == FlagCaptureResult.CANNOT_CAPTURE_OWN_FLAG) {
            player.sendMessage(this.messages.game.cannotCaptureOwnFlag);
        }

        else if (result == FlagCaptureResult.CAPTURED) {
            team.broadcastTitle(this.messages.game.yourFlagCapturedTitle, this.messages.game.yourFlagCapturedSubtitle);

            this.broadcastMessage(
                    this.messages.game.flagCaptured
                            .replace("{captured_team}", teamDisplay)
                            .replace("{captured_by}", playerDisplay));
        }

        else if (result == FlagCaptureResult.CAPTURED_DROPPED) {
            this.broadcastMessage(this.messages.game.flagCapturedDropped
                    .replace("{captured_team}", teamDisplay)
                    .replace("{captured_by}", playerDisplay));
        }

        else if (result == FlagCaptureResult.RETURNED) {
            team.broadcastTitle(this.messages.game.yourFlagReturnedTitle, this.messages.game.yourFlagReturnedSubtitle);

            this.broadcastMessage(this.messages.game.flagReturned
                    .replace("{captured_team}", teamDisplay)
                    .replace("{captured_by}", playerDisplay));
        }
    }

    public int getCurrentTime() {
        return Math.max(this.currentTime, 0);
    }

    public Flag getFlagAt(BlockPos pos) {
        for (CTFTeam team : this.server.getTeamManager().getTeams()) {
            Flag flag = team.getFlag();
            if (flag.isFlag(pos)) {
                return flag;
            }
        }

        return null;
    }

    public GamePhase getPhase() {
        return this.phase;
    }

    public int getTimeForPhase(GamePhase phase) {
        switch (phase) {
            case STARTING:
                return this.config.game.time.starting;
            case IN_GAME:
                return this.config.game.time.round;
            default:
                return 0;
        }
    }

    public boolean goal(Player player) {
        Flag flag = player.getCapturedFlag();
        if (flag == null) {
            return false;
        }

        CTFTeam playerTeam = player.getTeam();
        CTFTeam flagTeam = flag.getTeam();

        if (playerTeam != flagTeam) {
            player.addCapture();
            playerTeam.addPoint();

            String flagTeamDisplay = flagTeam.getColor().getChatColor() + flagTeam.getName();
            this.broadcastMessage(player.format(this.messages.game.goal).replace("{captured_team}", flagTeamDisplay));
            playerTeam.broadcastTitle(this.messages.game.goalTitle,
                    this.messages.game.goalSubtitle);
            flag.returnFlag();
            return true;
        }

        flag.returnFlag();
        return false;
    }

    public GameJoinResult joinPlayer(Player player) {
        CTFTeam team = player.getTeam();

        if (team == null && player.isOP()) {
            player.setState(PlayerState.SPECTATOR);
            player.teleport(this.arena.spectator);
            return GameJoinResult.SPECTATOR;
        }

        else if (this.phase == GamePhase.WAITING) {
            player.teleport(this.arena.lobby);

            if (team == null) {
                team = this.server.getTeamManager().getTeamWithLeastPlayers();
            }

            if (team != null) {
                team.joinPlayer(player);
                return GameJoinResult.JOINED;
            }

            player.sendMessage("&cNo hay ningun equipo disponible al cual unirte.");
            return GameJoinResult.SPECTATOR;
        }

        else if (this.phase == GamePhase.STARTING) {
            if (team != null) {
                player.teleport(this.arena.lobby);
                return GameJoinResult.JOINED;
            }
        }

        else if (this.phase == GamePhase.IN_GAME) {
            if (team != null) {
                player.respawn();
                return GameJoinResult.JOINED;
            }
        }

        else if (this.phase == GamePhase.SUMMARY) {
            if (team.isAlive()) {
                player.setState(PlayerState.SPECTATOR);
            } else {
                player.setState(PlayerState.DEAD);
            }

            return GameJoinResult.SPECTATOR;
        }

        player.teleport(this.arena.spectator);
        return GameJoinResult.SPECTATOR;
    }

    public void killTeam(CTFTeam team) {
        team.kill();
        this.server.getGame().broadcastMessage(
                this.messages.game.teamKilled.replace("{team_killed}", team.getDisplayName()));
    }

    public void leavePlayer(Player player) {
        Flag flag = player.getCapturedFlag();
        if (flag != null) {
            flag.returnFlag();
        }

        CTFTeam team = player.getTeam();
        if (team != null) {
            team.leavePlayer(player);
        }
    }

    public boolean isRunning() {
        return this.phase == GamePhase.IN_GAME;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
        this.currentTime = this.getTimeForPhase(phase);

        if (this.phase == GamePhase.IN_GAME) {
            this.broadcastMessage(this.messages.game.started);
            this.server.getLootBoxManager().resetAllCooldown();

            for (CTFTeam team : this.server.getTeamManager().getTeams()) {
                team.respawnAllPlayers();
                team.getFlag().spawnFlag();
            }
        }

        else if (this.phase == GamePhase.SUMMARY) {
            this.broadcastMessage(this.messages.game.finished);

            for (CTFTeam team : this.server.getTeamManager().getTeams()) {
                team.getFlag().returnFlag();
            }
        }
    }

    public void setTime(int time) {
        this.currentTime = time;
    }

    public boolean startRound() {
        if (this.phase != GamePhase.WAITING && this.phase != GamePhase.SUMMARY) {
            return false;
        }

        this.setPhase(GamePhase.STARTING);
        return true;
    }

    public boolean stopRound() {
        if (this.phase != GamePhase.IN_GAME) {
            return false;
        }

        this.setPhase(GamePhase.SUMMARY);
        return true;
    }

    void tickHandler() {
        int time = this.currentTime;

        if (time == 30 || time == 10 || (time <= 5 && time > 0)) {
            if (this.phase == GamePhase.STARTING) {
                this.broadcastMessage(this.messages.game.starting);
            } else if (this.phase == GamePhase.IN_GAME) {
                this.broadcastMessage(this.messages.game.finishing);
            }
        }
    }

    public void tick() {
        if (this.currentTime >= 0) {
            this.currentTime--;
        }

        if (this.currentTime < 0) {
            switch (this.phase) {
                case STARTING:
                    this.setPhase(GamePhase.IN_GAME);
                    break;
                case IN_GAME:
                    this.setPhase(GamePhase.SUMMARY);
                    break;
                default:
                    break;
            }
        } else {
            this.tickHandler();
        }

        for (CTFTeam team : this.server.getTeamManager().getTeams()) {
            team.tick();
        }

        for (Player player : this.server.getPlayerManager().getPlayers()) {
            player.tick();
            player.sendActionBar(this.messages.game.actionbar);
        }

        this.server.getLootBoxManager().tick();
    }
}
