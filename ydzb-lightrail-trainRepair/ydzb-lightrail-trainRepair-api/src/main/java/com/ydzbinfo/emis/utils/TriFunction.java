package com.ydzbinfo.emis.utils;

/**
 * 三参数方法
 * @param <T>
 * @param <U>
 * @param <V>
 * @author 张天可
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}
