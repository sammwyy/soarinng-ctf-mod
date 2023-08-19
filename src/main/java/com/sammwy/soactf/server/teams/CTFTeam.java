package com.sammwy.soactf.server.teams;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.sammwy.soactf.common.utils.ItemUtils;
import com.sammwy.soactf.common.utils.TextUtils;
import com.sammwy.soactf.server.chat.Color;
import com.sammwy.soactf.server.config.impl.CTFConfiguration;
import com.sammwy.soactf.server.flags.Flag;
import com.sammwy.soactf.server.players.Player;
import com.sammwy.soactf.server.players.PlayerState;
import com.sammwy.soactf.server.world.BlockPosition;
import com.sammwy.soactf.server.world.Position;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;

public class CTFTeam {
    private String id;
    private List<String> rawPlayers;
    private String name;
    private Color color;
    private String prefix;
    private boolean isAlive;
    private int points;
    private Position spawn;
    private BlockPosition flagSpawn;

    private CTFConfiguration config;
    private File dataFile;
    private Flag flag;
    private int maxPlayers;
    private boolean modified;
    private List<Player> players;
    private Scoreboard scoreboard;
    private Team scoreboardTeam;

    public CTFTeam(CTFConfiguration config, File dataFile, CTFTeamData data, Scoreboard scoreboard) {
        this.config = config;
        this.dataFile = dataFile;
        this.maxPlayers = config.teams.maxPlayers;
        this.modified = false;
        this.players = new ArrayList<>();
        this.scoreboard = scoreboard;
        this.scoreboardTeam = scoreboard.addTeam("ctf_team_" + data.id);

        this.scoreboardTeam.setFriendlyFireAllowed(false);
        this.scoreboardTeam.setShowFriendlyInvisibles(true);

        this.id = data.id;
        this.rawPlayers = data.players;
        this.setAlive(data.isAlive);
        this.setColor(Color.get(data.color));
        this.setName(data.name);
        this.setPrefix(data.prefix);
        this.setPoints(data.points);
        this.setSpawn(data.spawn);
        this.setFlagSpawn(data.flagSpawn);
    }

    public CTFTeam(CTFConfiguration config, File dataFile, Scoreboard scoreboard) {
        this(config, dataFile, CTFTeamData.fromFile(dataFile), scoreboard);
    }

    public void addPlayer(Player player) {
        this.rawPlayers.add(player.getUUID());
        this.joinPlayer(player);
        this.modified = true;
    }

    public int addPoint() {
        this.points++;
        return this.points;
    }

    public void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (Player player : this.players) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public void broadcastTitle(String title, String subtitle) {
        this.broadcastTitle(title, subtitle, 10, 40, 10);
    }

    public void broadcastMessage(String message) {
        for (Player player : this.players) {
            player.sendMessage(message);
        }
    }

    public ItemStack createFlagItem() {
        return new ItemStack(this.color.getItem(), 1);
    }

    public void delete() {
        this.dataFile.delete();

        for (Player player : this.players) {
            player.setTeam(null);

            if (this.flagSpawn != null) {
                this.flag.despawnFlag();
            }
        }
    }

    public void equipArmor(Player player) {
        ItemStack helmet = ItemUtils.createColorizedItem(Items.LEATHER_HELMET, this.color);
        ItemStack chestplate = ItemUtils.createColorizedItem(Items.LEATHER_CHESTPLATE, this.color);
        ItemStack leggings = ItemUtils.createColorizedItem(Items.LEATHER_LEGGINGS, this.color);
        ItemStack boots = ItemUtils.createColorizedItem(Items.LEATHER_BOOTS, this.color);
        player.getInventory().equipArmor(helmet, chestplate, leggings, boots);
    }

    public void equipArmorAll() {
        for (Player player : this.players) {
            this.equipArmor(player);
        }
    }

    public Color getColor() {
        return this.color;
    }

    public String getDisplayName() {
        String color = this.color.getChatColor();
        return color + this.name;
    }

    public Flag getFlag() {
        return this.flag;
    }

    public String getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public int getPoints() {
        return this.points;
    }

    public Position getSpawn() {
        return this.spawn;
    }

    public BlockPosition getFlagSpawn() {
        return this.flagSpawn;
    }

    public boolean hasCachedPlayer(String uuid) {
        for (String rawPlayer : this.rawPlayers) {
            if (rawPlayer.equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPlayer(Player player) {
        return this.players.contains(player);
    }

    public boolean hasPlayer(String uuid) {
        for (Player player : this.players) {
            if (player.getUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public boolean isFull() {
        return this.players.size() >= this.maxPlayers;
    }

    public void joinPlayer(Player player) {
        if (this.hasPlayer(player)) {
            return;
        }

        if (player.getTeam() != null && player.getTeam() != this) {
            player.getTeam().removePlayer(player);
        }

        this.players.add(player);
        this.scoreboard.addPlayerToTeam(player.getName(), scoreboardTeam);
        player.setTeam(this);

        if (this.isAlive()) {
            player.setState(PlayerState.ALIVE);
        } else {
            player.setState(PlayerState.DEAD);
        }
    }

    public void leavePlayer(Player player) {
        this.players.remove(player);
        this.scoreboard.removePlayerFromTeam(player.getName(), scoreboardTeam);
    }

    public void removePlayer(Player player) {
        this.leavePlayer(player);
        this.rawPlayers.remove(player.getUUID());
        this.modified = true;
    }

    public void save() throws IOException {
        if (this.dataFile == null)
            return;

        if (!this.dataFile.exists())
            this.dataFile.createNewFile();

        CTFTeamData data = new CTFTeamData();
        data.id = this.id;
        data.players = this.rawPlayers;
        data.isAlive = this.isAlive;
        data.color = this.color.name().toLowerCase();
        data.name = this.name;
        data.prefix = this.prefix;
        data.points = this.points;
        data.spawn = this.spawn;
        data.flagSpawn = this.flagSpawn;

        String json = data.toJSON();
        FileUtils.writeStringToFile(this.dataFile, json, StandardCharsets.UTF_8);
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

    public Team getScoreboardTeam() {
        return this.scoreboardTeam;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
        this.modified = true;
    }

    public void setColor(Color color) {
        this.color = color;
        this.scoreboardTeam.setColor(color.getChatFormat());
        this.modified = true;
    }

    public void setName(String name) {
        this.name = name;
        this.scoreboardTeam.setDisplayName(TextUtils.from(this.getDisplayName()));
        this.modified = true;
    }

    public void setPoints(int points) {
        this.points = points;
        this.modified = true;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        this.scoreboardTeam.setPrefix(TextUtils.from(prefix));
        this.modified = true;
    }

    public void setSpawn(Position spawn) {
        this.spawn = spawn;
        this.modified = true;
    }

    public void setFlagSpawn(BlockPosition flagSpawn) {
        if (this.flag != null) {
            this.flag.despawnFlag();
        }

        this.flag = new Flag(this, config.game.time.flagReturn, flagSpawn);
        this.flagSpawn = flagSpawn;
        this.modified = true;
    }

    public void teleportAllPlayers(Position position) {
        for (Player player : this.players) {
            player.teleport(position);
        }
    }

    public void teleportAllPlayersToSpawn() {
        this.teleportAllPlayers(this.spawn);
    }

    public void updateSuffix() {
        String suffix = null;

        if (this.isAlive()) {
            suffix = " \u00A78[\u00A7e" + this.points + "\u00A78]";
        } else {
            suffix = " \u00A78(\u00A74\u2620\u00A78)";
        }

        this.scoreboardTeam.setSuffix(TextUtils.from(suffix));
    }

    public void tick() {
        this.updateSuffix();
        this.saveIfModified();
        this.flag.tickReturnTimer();
    }

    public static CTFTeam create(CTFConfiguration config, String name, File dir, Scoreboard scoreboard, boolean save) {
        CTFTeamData data = CTFTeamData.create(name);
        File file = new File(dir, data.id + ".json");
        CTFTeam team = new CTFTeam(config, file, data, scoreboard);
        if (save)
            team.safeSave();
        return team;
    }
}
