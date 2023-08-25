package com.sammwy.soactf.common.utils;

import java.util.HashMap;
import java.util.Map;

public class GenericMap<K> {
    private Map<K, Object> map;

    public GenericMap() {
        this.map = new HashMap<>();
    }

    public boolean has(K key) {
        return this.map.containsKey(key);
    }

    public boolean getBool(K key, boolean defaultValue) {
        Object obj = this.map.getOrDefault(key, defaultValue);

        if (obj instanceof Boolean) {
            return (boolean) obj;
        } else {
            String raw = obj.toString();

            if (raw == "true" || raw == "yes" || raw == "on") {
                return true;
            } else if (raw == "false" || raw == "no" || raw == "off") {
                return false;
            } else {
                return defaultValue;
            }
        }
    }

    public int getInt(K key, int defaultValue) {
        Object obj = this.map.getOrDefault(key, defaultValue);
        if (obj instanceof Integer) {
            return (int) obj;
        } else {
            return Integer.parseInt(obj.toString());
        }
    }

    public String getString(K key, String defaultValue) {
        Object obj = this.map.getOrDefault(key, defaultValue);
        if (obj instanceof String) {
            return (String) obj;
        } else {
            return obj.toString();
        }
    }

    public Object put(K key, Object value) {
        return this.map.put(key, value);
    }
}
