package com.sammwy.soactf.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class Constants {
    public final static String MOD_ID = "soactf";
    public final static boolean IS_SERVER = FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    public final static boolean IS_CLIENT = !IS_SERVER;
}
