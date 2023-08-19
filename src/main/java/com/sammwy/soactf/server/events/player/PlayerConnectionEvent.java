package com.sammwy.soactf.server.events.player;

import com.sammwy.soactf.server.events.Event;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerConnectionEvent extends Event {
    private ClientConnection connection;
    private ServerPlayerEntity playerEntity;

    public PlayerConnectionEvent(ClientConnection connection, ServerPlayerEntity playerEntity) {
        this.connection = connection;
        this.playerEntity = playerEntity;
    }

    public ClientConnection getConnection() {
        return this.connection;
    }

    public ServerPlayerEntity getPlayerEntity() {
        return this.playerEntity;
    }
}
