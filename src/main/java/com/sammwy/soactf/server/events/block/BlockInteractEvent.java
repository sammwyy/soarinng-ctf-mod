package com.sammwy.soactf.server.events.block;

import com.sammwy.soactf.server.events.Cancellable;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class BlockInteractEvent extends Cancellable {
    private ServerPlayerEntity entity;
    private World world;
    private ItemStack stack;
    private Hand hand;
    private BlockHitResult hitResult;

    public BlockInteractEvent(ServerPlayerEntity entity, World world, ItemStack stack, Hand hand,
            BlockHitResult hitResult) {
        this.entity = entity;
        this.world = world;
        this.stack = stack;
        this.hand = hand;
        this.hitResult = hitResult;
    }

    public ServerPlayerEntity getEntity() {
        return entity;
    }

    public World getWorld() {
        return world;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Hand getHand() {
        return hand;
    }

    public BlockHitResult getHitResult() {
        return hitResult;
    }
}
