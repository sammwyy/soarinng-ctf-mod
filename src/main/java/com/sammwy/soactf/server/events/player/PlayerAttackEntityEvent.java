package com.sammwy.soactf.server.events.player;

import com.sammwy.soactf.server.events.Cancellable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class PlayerAttackEntityEvent extends Cancellable {
    private PlayerEntity player;
    private World world;
    private Hand hand;
    private Entity entity;
    private EntityHitResult result;

    public PlayerAttackEntityEvent(PlayerEntity player, World world, Hand hand, Entity entity,
            EntityHitResult result) {
        this.player = player;
        this.world = world;
        this.hand = hand;
        this.entity = entity;
        this.result = result;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }

    public World getWorld() {
        return this.world;
    }

    public Hand getHand() {
        return this.hand;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public EntityHitResult getResult() {
        return this.result;
    }
}
