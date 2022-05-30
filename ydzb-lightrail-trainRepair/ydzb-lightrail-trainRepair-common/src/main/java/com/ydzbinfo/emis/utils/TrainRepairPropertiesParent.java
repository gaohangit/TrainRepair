package com.ydzbinfo.emis.utils;

import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import com.ydzbinfo.emis.utils.entity.SerializableFunction;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 通用配置父类
 *
 * @param <T> 当前子类
 * @author 张天可
 * @since 2021/11/22
 */
public abstract class TrainRepairPropertiesParent<T extends TrainRepairPropertiesParent<T>> {
    public static String camelToMidline(String name) {
        // 快速检查
        if (StringUtils.isBlank(name)) {
            // 没必要转换
            return "";
        }
        int len = name.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append("-");
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * @param propertyGetter      要校验属性的getter方法
     * @param propertyDescription 属性描述
     */
    @SuppressWarnings("unchecked")
    protected <R> void checkProperty(SerializableFunction<T, R> propertyGetter, String propertyDescription) {
        if (propertyGetter.apply((T) this) == null) {
            throw new RuntimeException("未配置【" + propertyDescription + "】: " + getPropertyPath(propertyGetter));
        }
    }

    /**
     * @param propertyGetter 当前子类属性的getter方法
     * @return 属性的路径
     */
    public <R> String getPropertyPath(SerializableFunction<T, R> propertyGetter) {
        String propertyName = ReflectUtil.getPropertyNameByGetter(propertyGetter, this.getClass(), () -> new IllegalArgumentException("请传入当前类的getter方法引用"));
        return getPrefix(this.getClass()) + "." + TrainRepairPropertiesParent.camelToMidline(propertyName);
    }

    public static <T extends TrainRepairPropertiesParent<?>> String getPrefix(Class<T> tClass) {
        ConfigurationProperties configurationProperties = AnnotationUtils.findAnnotation(tClass, ConfigurationProperties.class);
        return Objects.requireNonNull(configurationProperties).prefix();
    }
}
