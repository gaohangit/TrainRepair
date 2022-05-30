package com.ydzbinfo.emis.utils.entity;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 可序列化的function，用于根据类的属性的lambda方法获取类的属性信息
 *
 * @author 张天可
 * @since 2021/11/25
 */
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {
}
