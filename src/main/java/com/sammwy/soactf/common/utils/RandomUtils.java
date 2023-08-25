package com.sammwy.soactf.common.utils;

public class RandomUtils {
    public static int getRandomInt(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }
}
