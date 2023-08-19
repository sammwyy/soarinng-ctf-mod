package com.sammwy.soactf.common.utils;

import java.io.File;

import net.fabricmc.loader.api.FabricLoader;

public class FabricUtils {
    public static File getConfigDir() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

    public static File getConfigDir(String subDir) {
        File sub = new File(getConfigDir(), subDir);
        if (!sub.exists())
            sub.mkdirs();
        return sub;
    }

    public static File getConfigDir(String subDir, String childSubDir) {
        File sub = new File(getConfigDir(subDir), childSubDir);
        if (!sub.exists())
            sub.mkdirs();
        return sub;
    }
}
