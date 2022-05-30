package com.ydzbinfo.emis.utils;

/**
 * 三参数消费者
 * @param <T>
 * @param <U>
 * @param <V>
 * @author 张天可
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}
