package com.sammwy.soactf.common.utils;

import java.lang.reflect.InvocationTargetException;

public class ReflectionUtils {
    public static Object instantiate(Class<?> clazz) {
        try {
            return clazz.getConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T instantiateP(Class<T> clazz) {
        return (T) instantiate(clazz);
    }
}
