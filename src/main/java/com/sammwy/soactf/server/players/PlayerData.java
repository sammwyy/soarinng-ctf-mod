package com.sammwy.soactf.server.players;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.google.gson.annotations.Expose;
import com.sammwy.soactf.server.config.Configuration;

public class PlayerData {
    @Expose
    public String uuid;

    @Expose
    public String name;

    @Expose
    public String teamID;

    @Expose
    public PlayerState state = PlayerState.SPECTATOR;

    @Expose
    public int kills;

    @Expose
    public int deaths;

    @Expose
    public int captures;

    @Expose
    public int returnedFlags;

    public String toJSON() {
        return Configuration.GSON.toJson(this);
    }

    public static PlayerData fromPlayer(Player player) {
        PlayerData data = new PlayerData();
        data.uuid = player.getUUID();
        data.name = player.getName();
        data.teamID = player.getTeam() != null ? player.getTeam().getID() : null;
        data.state = player.getState();
        data.kills = player.getKills();
        data.deaths = player.getDeaths();
        data.captures = player.getCaptures();
        data.returnedFlags = player.getReturnedFlags();
        return data;
    }

    public static PlayerData fromJSON(String json) {
        return Configuration.GSON.fromJson(json, PlayerData.class);
    }

    public static PlayerData fromFile(File file) {
        if (!file.exists()) {
            return null;
        }

        try {
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            return fromJSON(json);
        } catch (IOException e) {
            return null;
        }
    }
}
