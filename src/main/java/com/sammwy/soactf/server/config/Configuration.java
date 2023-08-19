package com.sammwy.soactf.server.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sammwy.soactf.common.utils.ReflectionUtils;

public class Configuration {
    public static Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    private File file;

    public File getFile() {
        return this.file;
    }

    public boolean save() {
        try {
            String raw = GSON.toJson(this);
            FileUtils.writeStringToFile(this.file, raw, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setFile(File file) {
        this.file = file;
    }

    public static <T extends Configuration> T instantiate(Class<T> clazz) {
        return ReflectionUtils.instantiateP(clazz);
    }

    public static <T extends Configuration> T load(File file, Class<T> clazz) throws IOException {
        String raw = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        return GSON.fromJson(raw, clazz);
    }

    public static <T extends Configuration> T safeLoad(File file, Class<T> clazz) {
        T config = null;

        try {
            config = load(file, clazz);
        } catch (IOException e) {
            config = instantiate(clazz);
        }

        config.setFile(file);
        return config;
    }
}
