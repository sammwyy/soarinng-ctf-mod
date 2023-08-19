package com.sammwy.soactf.common.utils;

import com.sammwy.soactf.server.world.BlockPosition;
import com.sammwy.soactf.server.world.Position;

import net.minecraft.server.world.ServerWorld;

public class PositionUtils {
    public static BlockPosition getSafeBlockPosition(ServerWorld world, BlockPosition position) {
        BlockPosition safePosition = new BlockPosition(position);

        while (world.getBlockState(safePosition.toBlockPos()).isAir() && safePosition.y >= 0) {
            safePosition.subtract(0, 1, 0);
        }

        return safePosition.y >= 0 ? safePosition : null;
    }

    public static BlockPosition getSafeBlockPosition(ServerWorld world, Position position) {
        return getSafeBlockPosition(world, BlockPosition.fromBlockPos(position.toBlockPos()));
    }
}
