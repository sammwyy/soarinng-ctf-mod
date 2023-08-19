package com.sammwy.soactf.server.events.player;

import com.sammwy.soactf.server.events.Event;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PlayerDisconnectEvent extends Event {
    private ServerPlayerEntity entity;
    private Text reason;

    public PlayerDisconnectEvent(ServerPlayerEntity entity, Text reason) {
        this.entity = entity;
        this.reason = reason;
    }

    public ServerPlayerEntity getPlayerEntity() {
        return this.entity;
    }

    public Text getReason() {
        return this.reason;
    }
}
