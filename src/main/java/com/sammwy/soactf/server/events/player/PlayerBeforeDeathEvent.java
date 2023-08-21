package com.sammwy.soactf.server.events.player;

import com.sammwy.soactf.server.events.Cancellable;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerBeforeDeathEvent extends Cancellable {
    private ServerPlayerEntity entity;
    private DamageSource source;
    private float damage;

    public PlayerBeforeDeathEvent(ServerPlayerEntity entity, DamageSource source, float damage) {
        this.entity = entity;
        this.source = source;
        this.damage = damage;
    }

    public ServerPlayerEntity getEntity() {
        return entity;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getDamage() {
        return damage;
    }
}
