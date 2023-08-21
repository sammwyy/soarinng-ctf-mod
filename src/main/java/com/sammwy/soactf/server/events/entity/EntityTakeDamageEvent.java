package com.sammwy.soactf.server.events.entity;

import com.sammwy.soactf.server.events.Cancellable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class EntityTakeDamageEvent extends Cancellable {
    private LivingEntity entity;
    private DamageSource source;
    private float amount;

    public EntityTakeDamageEvent(LivingEntity entity, DamageSource source, float amount) {
        this.entity = entity;
        this.source = source;
        this.amount = amount;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public DamageSource getSource() {
        return this.source;
    }

    public float getAmount() {
        return this.amount;
    }
}
