package com.sammwy.soactf.common.utils;

import com.sammwy.soactf.server.world.BlockPosition;

import net.minecraft.server.world.ServerWorld;

public class PositionUtils {
    public static BlockPosition getSafeBlockPosition(ServerWorld world, BlockPosition position) {
        BlockPosition safePosition = new BlockPosition(position);
        int max = 10;

        while (world.getBlockState(safePosition.toBlockPos()).isAir() && safePosition.y >= 0 && max > 0) {
            safePosition.subtract(0, 1, 0);
            max--;
        }

        return safePosition.y >= 0 ? safePosition.add(0, 1, 0) : null;
    }
}
