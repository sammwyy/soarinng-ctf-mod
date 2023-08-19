package com.sammwy.soactf.server.players;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.sammwy.soactf.common.utils.ItemUtils;
import com.sammwy.soactf.server.SoaCTFServer;
import com.sammwy.soactf.server.flags.Flag;
import com.sammwy.soactf.server.game.Game;
import com.sammwy.soactf.server.teams.CTFTeam;

import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class Player extends PlayerBase {
    // Aux variables.
    private int respawnTime = -1;

    // Statistics.
    private int kills;
    private int deaths;
    private int captures;
    private int returnedFlags;

    // State.
    private SoaCTFServer server;
    private File dataFile;
    private boolean modified;
    private CTFTeam team;
    private Flag capturedFlag;
    private PlayerState state = PlayerState.SPECTATOR;

    public Player(SoaCTFServer server, File dataFile, PlayerData data, ClientConnection connection,
            ServerPlayerEntity entity) {
        super(connection, entity);
        this.server = server;
        this.dataFile = dataFile;
        this.deserializeFrom(data);
    }

    public Player(SoaCTFServer server, File dataFile, ClientConnection connection,
            ServerPlayerEntity entity) {
        this(server, dataFile, PlayerData.fromFile(dataFile), connection, entity);
    }

    public void addKill() {
        this.kills++;
        this.modified = true;
    }

    public void addDeath() {
        this.deaths++;
        this.modified = true;
    }

    public void addCapture() {
        this.captures++;
        this.modified = true;
    }

    public void addReturnedFlag() {
        this.returnedFlags++;
        this.modified = true;
    }

    public void deserializeFrom(PlayerData data) {
        if (data == null) {
            return;
        }

        this.team = data.teamID != null ? this.server.getTeamManager().getTeam(data.teamID) : null;
        this.state = data.state;
        this.kills = data.kills;
        this.deaths = data.deaths;
        this.captures = data.captures;
        this.returnedFlags = data.returnedFlags;
    }

    public String format(String string) {
        Game game = this.server.getGame();
        CTFTeam team = this.getTeam();

        if (game != null) {
            string = string.replace("{game_time}", String.valueOf(game.getCurrentTime()))
                    .replace("{game_phase}", game.getPhase().name());
        }

        if (team != null) {
            string = string.replace("{team_points}", String.valueOf(team.getPoints()))
                    .replace("{team_name}", team.getName())
                    .replace("{team_prefix}", team.getPrefix())
                    .replace("{team_color}", team.getColor().getChatColor());

            Player captured = team.getFlag().getCapturedBy();
            Player returned = team.getFlag().getReturnedBy();

            if (captured != null) {
                string = string.replace("{team_captured_by}", captured.getDisplayName());
            }

            if (returned != null) {
                string = string.replace("{team_returned_by}", returned.getDisplayName());
            }
        }

        return string.replace("{player_name}", this.getName())
                .replace("{player_captures}", String.valueOf(captures))
                .replace("{player_deaths}", String.valueOf(deaths))
                .replace("{player_kills}", String.valueOf(kills))
                .replace("{player_respawn_time}", String.valueOf(respawnTime))
                .replace("{player_returned_flags}", String.valueOf(returnedFlags))
                .replace("{player_state}", state.name());
    }

    public int getCaptures() {
        return this.captures;
    }

    public Flag getCapturedFlag() {
        return this.capturedFlag;
    }

    public String getDisplayName() {
        if (this.team == null) {
            return this.getName();
        }

        return this.team.getColor().getChatColor() + this.getName();
    }

    public CTFTeam getTeam() {
        return this.team;
    }

    public int getKills() {
        return this.kills;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public int getReturnedFlags() {
        return this.returnedFlags;
    }

    public PlayerData getSerializableData() {
        return PlayerData.fromPlayer(this);
    }

    public PlayerState getState() {
        return this.state;
    }

    public boolean hasCapturedFlag() {
        return this.capturedFlag != null;
    }

    public boolean isDead() {
        return this.state == PlayerState.DEAD;
    }

    public boolean isPlaying() {
        return this.team != null && this.state == PlayerState.ALIVE;
    }

    public boolean isRespawning() {
        return this.state == PlayerState.RESPAWNING;
    }

    public boolean isSpectator() {
        return this.state == PlayerState.SPECTATOR;
    }

    public void kill(boolean gameOver) {
        this.setState(gameOver ? PlayerState.DEAD : PlayerState.RESPAWNING);
        this.setHealth(20);
        this.modified = true;
    }

    public void respawn() {
        this.setState(PlayerState.ALIVE);
        this.setHealth(20);
        if (this.team != null) {
            this.team.equipArmor(this);
            this.teleport(this.team.getSpawn());
        }
        this.modified = true;
    }

    public void save() throws IOException {
        if (!this.dataFile.exists()) {
            this.dataFile.getParentFile().mkdirs();
            this.dataFile.createNewFile();
        }

        PlayerData data = this.getSerializableData();
        FileUtils.writeStringToFile(this.dataFile, data.toJSON(), StandardCharsets.UTF_8);
    }

    public void safeSave() {
        try {
            this.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveIfModified() {
        if (this.modified) {
            this.safeSave();
            this.modified = false;
        }
    }

    @Override
    public void sendActionBar(String message) {
        super.sendActionBar(this.format(message));
    }

    @Override
    public void sendMessage(String message) {
        super.sendMessage(this.format(message));
    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        super.sendTitle(this.format(title), this.format(subtitle), fadeInTicks, stayTicks, fadeOutTicks);
    }

    public void setTeam(CTFTeam team) {
        this.team = team;
        this.modified = true;
    }

    public void setCapturedFlag(Flag flag) {
        this.capturedFlag = flag;
        this.modified = true;

        if (flag != null) {
            this.getInventory().setItem(3, flag.getItemStack());
            this.getInventory().setHelmet(flag.getItemStack());
        } else {
            this.getInventory().clearItem(3);
            this.getInventory()
                    .setHelmet(ItemUtils.createColorizedItem(Items.LEATHER_HELMET, this.team.getColor().getDyeColor()));
        }
    }

    public void setRespawnTime(int ticks) {
        this.respawnTime = ticks;
        this.modified = true;
    }

    public void setState(PlayerState state) {
        this.state = state;
        this.modified = true;

        if (this.isSpectator()) {
            this.setGameMode(GameMode.SPECTATOR);
        } else {
            this.setGameMode(GameMode.ADVENTURE);
        }
    }

    public void tickRespawnTime() {
        if (this.respawnTime > 0) {
            this.respawnTime--;
        }
    }

    public void tick() {
        this.tickRespawnTime();
        this.saveIfModified();
    }
}
