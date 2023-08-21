package com.sammwy.soactf.server.events.player;

import com.sammwy.soactf.server.events.Event;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerDeathEvent extends Event {
    private ServerPlayerEntity entity;
    private DamageSource source;

    public PlayerDeathEvent(ServerPlayerEntity entity, DamageSource source) {
        this.entity = entity;
        this.source = source;
    }

    public ServerPlayerEntity getEntity() {
        return entity;
    }

    public DamageSource getSource() {
        return source;
    }
}
