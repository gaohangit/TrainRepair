package com.ydzbinfo.emis.utils.entity;

import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 通过反射打印实体属性信息
 *
 * @author 张天可
 */
public class ToStringUtil {
    /**
     * 打印对象属性信息
     *
     * @param entity 实体实例
     * @return
     */
    public static String toString(Object entity) {
        if (entity == null) {
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            Class<?> entityClass = entity.getClass();
            Field[] fields = ReflectUtil.getEntityFields(entityClass);
            stringBuilder.append(entityClass.getSimpleName());
            stringBuilder.append("{");
            boolean firstAdded = false;
            for (Field field : fields) {
                PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(entityClass, field.getName());
                try {
                    if (pd != null) {
                        if (firstAdded) {
                            stringBuilder.append(", ");
                        } else {
                            firstAdded = true;
                        }
                        stringBuilder.append(field.getName());
                        stringBuilder.append("=");
                        stringBuilder.append(pd.getReadMethod().invoke(entity));
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("获取对象属性值失败", e);
                }
            }
            stringBuilder.append("}");
            return stringBuilder.toString();
        }
    }
}
