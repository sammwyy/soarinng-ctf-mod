package com.sammwy.soactf.server.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private File configPath;
    private Map<String, Configuration> cached;

    public ConfigManager(File configPath) {
        this.configPath = configPath;
        this.cached = new HashMap<>();
    }

    public <T extends Configuration> T load(String name, Class<T> clazz) {
        if (name.contains("/..") || name.contains("\\..") || name.startsWith(("..")) || name.startsWith("/")
                || name.startsWith("\\") || name.contains(":") || name.contains("*") || name.contains("?")
                || name.contains("\"") || name.contains("<") || name.contains(">") || name.contains("|")) {
            throw new IllegalArgumentException("Illegal config name");
        }

        File file = new File(this.configPath, name);
        T config = Configuration.safeLoad(file, clazz);
        if (!file.exists()) {
            config.save();
        }
        return config;
    }

    public <T extends Configuration> T getConfig(String name, Class<T> clazz) {
        if (this.cached.containsKey(name)) {
            Object config = this.cached.get(name);

            if (config.getClass().equals(clazz)) {
                T configObject = clazz.cast(config);
                this.cached.put(name, configObject);
                return configObject;
            }
        }

        return this.load(name, clazz);
    }
}
