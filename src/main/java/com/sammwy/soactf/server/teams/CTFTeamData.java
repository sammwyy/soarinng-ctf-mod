package com.sammwy.soactf.server.teams;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.annotations.Expose;
import com.sammwy.soactf.server.config.Configuration;
import com.sammwy.soactf.server.world.BlockPosition;
import com.sammwy.soactf.server.world.Position;

public class CTFTeamData {
    @Expose
    public String id;

    @Expose
    public String name;

    @Expose
    public String prefix;

    @Expose
    public List<String> players;

    @Expose
    public String color;

    @Expose
    public Position spawn;

    @Expose
    public BlockPosition flagSpawn;

    @Expose
    public int points = 0;

    @Expose
    public boolean isAlive;

    public String toJSON() {
        return Configuration.GSON.toJson(this);
    }

    public static CTFTeamData create(String name) {
        String id = name.toLowerCase().replace(" ", "_");

        CTFTeamData data = new CTFTeamData();
        data.id = id;
        data.name = name;
        data.prefix = name + " ";
        data.color = "white";
        data.players = new ArrayList<>();
        data.spawn = new Position();
        data.flagSpawn = new BlockPosition();
        data.points = 0;
        data.isAlive = true;
        return data;
    }

    public static CTFTeamData fromJSON(String json) {
        return Configuration.GSON.fromJson(json, CTFTeamData.class);
    }

    public static CTFTeamData fromFile(File file) {
        try {
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            return fromJSON(json);
        } catch (IOException e) {
            return null;
        }
    }
}
