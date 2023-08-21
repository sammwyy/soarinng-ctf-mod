package com.sammwy.soactf.server.events.player;

import com.sammwy.soactf.server.events.Event;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerKIllPlayerEvent extends Event {
    private ServerPlayerEntity killer;
    private ServerPlayerEntity victim;
    private DamageSource source;

    public PlayerKIllPlayerEvent(ServerPlayerEntity killer, ServerPlayerEntity victim, DamageSource source) {
        this.killer = killer;
        this.victim = victim;
        this.source = source;
    }

    public ServerPlayerEntity getKiller() {
        return killer;
    }

    public ServerPlayerEntity getVictim() {
        return victim;
    }

    public DamageSource getSource() {
        return source;
    }
}
