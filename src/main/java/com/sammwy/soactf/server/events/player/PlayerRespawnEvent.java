package com.sammwy.soactf.server.events.player;

import com.sammwy.soactf.server.events.Event;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerRespawnEvent extends Event {
    private ServerPlayerEntity oldEntity;
    private ServerPlayerEntity newEntity;
    private boolean alive;

    public PlayerRespawnEvent(ServerPlayerEntity oldEntity, ServerPlayerEntity newEntity, boolean alive) {
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
        this.alive = alive;
    }

    public ServerPlayerEntity getOldEntity() {
        return oldEntity;
    }

    public ServerPlayerEntity getNewEntity() {
        return newEntity;
    }

    public boolean isAlive() {
        return alive;
    }
}
