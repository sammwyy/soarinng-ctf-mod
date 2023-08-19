package com.sammwy.soactf.common.utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArrayUtils {
    public static <E, V> List<V> map(List<E> list, Function<E, V> function) {
        return list.stream().map(function).collect(Collectors.toList());
    }

    public static <E, V> List<V> map(E[] list, Function<E, V> function) {
        return map(Arrays.asList(list), function);
    }

    public static <T> void iter(Iterable<T> list, Function<T, ?> function) {
        for (T element : list) {
            function.apply(element);
        }
    }
}
