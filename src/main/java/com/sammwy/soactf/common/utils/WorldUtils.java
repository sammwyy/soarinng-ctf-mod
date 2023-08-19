package com.sammwy.soactf.common.utils;

import net.minecraft.server.world.ServerWorld;

public class WorldUtils {
    private static ServerWorld DEFAULT_WORLD;

    public static ServerWorld getDefaultWorld() {
        return DEFAULT_WORLD;
    }

    public static void setDefaultWorld(ServerWorld world) {
        DEFAULT_WORLD = world;
    }
}
