package com.sammwy.soactf.server.players;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.sammwy.soactf.server.SoaCTFServer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerManager {
    private final SoaCTFServer server;
    private final File playersDir;

    private Map<String, Player> players;

    public PlayerManager(SoaCTFServer server, File playersDir) {
        this.server = server;
        this.playersDir = playersDir;

        this.players = new HashMap<>();
    }

    public void addPlayer(Player player) {
        this.players.put(player.getUUID(), player);
    }

    public Player addPlayer(ClientConnection connection, ServerPlayerEntity entity) {
        File dataFile = new File(this.playersDir, entity.getUuidAsString() + ".json");
        Player player = new Player(this.server, dataFile, connection, entity);
        this.addPlayer(player);
        return player;
    }

    public Collection<Player> getPlayers() {
        return this.players.values();
    }

    public Player getPlayer(String uuid) {
        return this.players.get(uuid);
    }

    public Player getPlayerByName(String username) {
        return this.players.values().stream().filter(player -> player.getName().equalsIgnoreCase(username)).findFirst()
                .orElse(null);
    }

    public Player getPlayer(PlayerEntity entity) {
        return this.getPlayer(entity.getUuidAsString());
    }

    public Player removePlayer(String uuid) {
        return this.players.remove(uuid);
    }

    public Player removePlayer(ServerPlayerEntity entity) {
        return this.removePlayer(entity.getUuidAsString());
    }
}
