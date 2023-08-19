package com.sammwy.soactf.server.events.block;

import com.sammwy.soactf.server.events.Cancellable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class BlockBreakEvent extends Cancellable {
    private ServerPlayerEntity entity;
    private ServerWorld world;
    private BlockPos blockPos;
    private BlockState blockState;
    private BlockEntity blockEntity;
    private Block block;

    public BlockBreakEvent(ServerPlayerEntity entity, ServerWorld world, BlockPos pos, BlockState state,
            BlockEntity blockEntity, Block block) {
        this.entity = entity;
        this.world = world;
        this.blockPos = pos;
        this.blockState = state;
        this.blockEntity = blockEntity;
        this.block = block;
    }

    public ServerPlayerEntity getEntity() {
        return this.entity;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public BlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public Block getBlock() {
        return this.block;
    }
}
