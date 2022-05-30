package com.ydzbinfo.emis.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 枚举相关工具
 *
 * @author 张天可
 */
public class EnumUtils {

    protected static final Logger logger = LoggerFactory.getLogger(EnumUtils.class);

    /**
     * 根据唯一值寻找枚举值
     *
     * @param enumClass 枚举类型
     * @param getKey    获取枚举值的唯一值
     * @param getValue  获取枚举值或者其属性
     * @param key       唯一值
     * @param <T>       枚举类型
     * @param <K>       唯一值类型
     * @param <V>       结果类型
     * @return 找到的结果
     * @author 张天可
     */
    public static <T, K, V> V findValue(Class<T> enumClass, Function<T, K> getKey, Function<T, V> getValue, K key) {
        try {
            T[] enumMembers = enumClass.getEnumConstants();
            for (T enumMember : enumMembers) {
                if (getKey.apply(enumMember).equals(key)) {
                    return getValue.apply(enumMember);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T, K> T findEnum(Class<T> enumClass, Function<T, K> getKeyFromEnum, K key) {
        return findValue(enumClass, getKeyFromEnum, v -> v, key);
    }

    public static Map<String, Object> toMap(Enum<?> entity) {
        try {
            PropertyDescriptor[] propertyDescriptors = CommonUtils.getFilteredPropertyDescriptors(entity.getClass());
            Map<String, Object> map = new HashMap<>();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                map.put(propertyDescriptor.getName(), propertyDescriptor.getReadMethod().invoke(entity));
            }
            return map;
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("toMap调用失败", e);
            return null;
        }
    }

    /**
     * 静态初始化枚举类，用于手动触发执行枚举的静态代码
     */
    public static <T extends Enum<T>> void staticInitializeEnum(Class<T> enumClass) {
        try {
            Enum.valueOf(enumClass, "");
        } catch (IllegalArgumentException ignored) {
        }
    }
}
