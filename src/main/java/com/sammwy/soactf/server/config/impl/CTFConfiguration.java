package com.sammwy.soactf.server.config.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sammwy.soactf.server.config.Configuration;

public class CTFConfiguration extends Configuration {
    // game
    public static class GameConfig {
        // game.time
        public static class Time {
            @Expose // game.time.respawn
            public int respawn = 10;

            @Expose // game.time.starting
            public int starting = 30;

            @Expose // game.time.round
            public int round = 900;

            @Expose // game.time.flag_return
            @SerializedName("flag_return")
            public int flagReturn = 120;
        }

        @Expose // game.time
        public Time time = new Time();

        @Expose // game.disable_hunger
        @SerializedName("disable_hunger")
        public boolean disableHunger = true;

        @Expose // game.fall_damage
        @SerializedName("fall_damage")
        public boolean fallDamage = false;

        @Expose // game.speed_boost
        @SerializedName("speed_boost")
        public int speedBoost = 1;

        @Expose // game.jump_boost
        @SerializedName("jump_boost")
        public int jumpBoost = 1;
    }

    // loot
    public static class LootConfig {
        @Expose // loot.items
        public List<String> items = new ArrayList<>();

        @Expose // loot.respawn_cooldown
        @SerializedName("respawn_cooldown")
        public int respawnCooldown = 30;
    }

    // teams
    public static class TeamsConfig {
        @Expose // teams.max_players
        @SerializedName("max_players")
        public int maxPlayers = 10;
    }

    @Expose
    public GameConfig game = new GameConfig();

    @Expose
    public LootConfig loots = new LootConfig();

    @Expose
    public TeamsConfig teams = new TeamsConfig();
}
