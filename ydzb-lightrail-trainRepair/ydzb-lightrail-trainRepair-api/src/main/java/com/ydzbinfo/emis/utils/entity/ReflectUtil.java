package com.ydzbinfo.emis.utils.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.entity.Column;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 反射工具类
 *
 * @author 张天可
 */
public class ReflectUtil {
    private static final Map<Class<?>, Field[]> fieldsCache = new ConcurrentHashMap<>();

    public static Field[] getEntityFields(Class<?> entityClass) {
        if (fieldsCache.get(entityClass) != null) {
            return fieldsCache.get(entityClass);
        } else {
            Field[] fields = {};
            while (entityClass != null && !entityClass.isInterface() && entityClass.getSuperclass() != null) {
                fields = (Field[]) ArrayUtils.addAll(fields, entityClass.getDeclaredFields());
                entityClass = entityClass.getSuperclass();
            }
            fieldsCache.putIfAbsent(entityClass, fields);
            return fields;
        }
    }

    public static Field getEntityFieldNoCache(Class<?> entityClass, String property) {
        Field field = null;
        while (field == null && !entityClass.isInterface() && entityClass.getSuperclass() != null) {
            try {
                field = entityClass.getDeclaredField(property);
            } catch (NoSuchFieldException ignored) {
                if (property.length() > 0 && Character.isUpperCase(property.charAt(0))) {
                    field = getEntityFieldNoCache(entityClass, Character.toLowerCase(property.charAt(0)) + property.substring(1));
                }
                if (field == null) {
                    // field名与属性名可能不完全一致
                    for (Field declaredField : entityClass.getDeclaredFields()) {
                        if (declaredField.getName().equalsIgnoreCase(property)) {
                            field = declaredField;
                            break;
                        }
                    }
                }
            }
            entityClass = entityClass.getSuperclass();
        }
        return field;
    }

    private static final Map<Class<?>, Map<String, Field>> fieldCacheByProperty = new ConcurrentHashMap<>();

    public static Field getEntityField(Class<?> entityClass, String property) {
        if (!fieldCacheByProperty.containsKey(entityClass)) {
            fieldCacheByProperty.put(entityClass, new ConcurrentHashMap<>());
        }
        if (fieldCacheByProperty.get(entityClass).containsKey(property)) {
            return fieldCacheByProperty.get(entityClass).get(property);
        }
        Field field = getEntityFieldNoCache(entityClass, property);
        if (field != null) {
            fieldCacheByProperty.get(entityClass).put(property, field);
        }
        return field;
    }

    /**
     * 根据field上的注解判断对应数据库属性名
     *
     * @param field
     * @return
     */
    public static String getColumnNameFromField(Field field) {
        if (field == null) {
            return null;
        }
        String columnName = null;
        if (field.isAnnotationPresent(TableField.class)) {
            columnName = field.getAnnotation(TableField.class).value();
        } else if (field.isAnnotationPresent(TableId.class)) {
            columnName = field.getAnnotation(TableId.class).value();
        }
        return columnName;
    }

    /**
     * 获取id列名
     */
    public static Column[] getIdColumns(Class<?> aClass) {
        return Arrays.stream(getEntityFields(aClass)).filter(
            field -> field.isAnnotationPresent(TableId.class)
        ).map(
            v -> {
                TableId tableId = v.getAnnotation(TableId.class);
                return Column.create().column(tableId.value()).as(v.getName());
            }
        ).toArray(Column[]::new);
    }

    public static String getTableName(Class<?> tableClass) {
        if (tableClass != null && tableClass.isAnnotationPresent(TableName.class)) {
            return tableClass.getAnnotation(TableName.class).value();
        } else {
            return null;
        }
    }

    /**
     * 获取指定field的值
     *
     * @param entity
     * @param field
     * @return
     */
    public static Object getValue(Object entity, Field field) {
        return getValue(entity, field.getName());
    }

    /**
     * 获取指定属性名的属性值
     *
     * @param entity
     * @param propertyName
     * @return
     */
    public static Object getValue(Object entity, String propertyName) {
        if (entity == null) {
            return null;
        }
        Class<?> entityClass = entity.getClass();
        try {
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(entityClass, propertyName);
            if (pd != null) {
                return pd.getReadMethod().invoke(entity);
            } else {
                return null;
            }
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置指定field的值
     *
     * @param entity
     * @param field
     * @param value
     */
    public static void setValue(Object entity, Field field, Object value) {
        setValue(entity, field.getName(), value);
    }

    /**
     * 设置指定属性名的值
     *
     * @param entity
     * @param propertyName
     * @param value
     */
    public static void setValue(Object entity, String propertyName, Object value) {
        if (entity == null) {
            return;
        }
        Class<?> entityClass = entity.getClass();
        try {
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(entityClass, propertyName);
            if (pd != null) {
                pd.getWriteMethod().invoke(entity, value);
            }
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static SerializedLambda getSerializedLambdaFromPropertyGetter(SerializableFunction<?, ?> propertyGetter) {
        try {
            Method writeReplaceMethod = propertyGetter.getClass().getDeclaredMethod("writeReplace");
            writeReplaceMethod.setAccessible(Boolean.TRUE);
            return (SerializedLambda) writeReplaceMethod.invoke(propertyGetter);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("获取类属性方法失败", e);
        }
    }

    public static Class<?> getClassBySerializedLambda(SerializedLambda serializedLambda) {
        try {
            return Class.forName(serializedLambda.getImplClass().replace("/", "."), false, ClassUtils.getDefaultClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("属性方法实现类失败", e);
        }
    }

    public static String getPropertyNameByGetterNameAndClass(String readMethodName, Class<?> entityClass) {
        for (PropertyDescriptor propertyDescriptor : BeanUtils.getPropertyDescriptors(entityClass)) {
            if (readMethodName.equals(propertyDescriptor.getReadMethod().getName())) {
                return propertyDescriptor.getName();
            }
        }
        return null;
    }

    public static <T, R> String getPropertyNameByGetter(SerializableFunction<T, R> propertyGetter, Class<?> targetClass, Supplier<? extends RuntimeException> exceptionGetter) {
        assert exceptionGetter != null;
        SerializedLambda serializedLambda = getSerializedLambdaFromPropertyGetter(propertyGetter);
        Class<?> entityClass = getClassBySerializedLambda(serializedLambda);
        if (!entityClass.isAssignableFrom(targetClass)) {
            throw exceptionGetter.get();
        }
        String propertyName = getPropertyNameByGetterNameAndClass(serializedLambda.getImplMethodName(), entityClass);
        if (propertyName != null) {
            return propertyName;
        }
        throw exceptionGetter.get();
    }

    private static ParameterizedType findParameterizedType(Class<?> objClass, boolean onlyInterface) {
        if (!onlyInterface) {
            Type genericSuperclass = objClass.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                return (ParameterizedType) genericSuperclass;
            }
        }
        for (Type genericInterface : objClass.getGenericInterfaces()) {
            if (genericInterface instanceof Class) {
                ParameterizedType type = findParameterizedType((Class<?>) genericInterface, onlyInterface);
                if (type != null) {
                    return type;
                }
            } else if (genericInterface instanceof ParameterizedType) {
                return (ParameterizedType) genericInterface;
            }
        }
        return null;
    }

    /**
     * 获取对象的类的接口类或超类的第一个参数泛型
     * 要求当前类在此参数泛型上是确定的
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getFirstTypeArgument(Object obj, boolean onlyInterface) {
        Class<?> objClass = obj.getClass();
        try {
            //处理spring aop增强的类 $$EnhancerBySpringCGLIB$$
            Method getTargetSourceMethod = objClass.getMethod("getTargetSource");
            Object innerObj = getTargetSourceMethod.invoke(obj);
            if (innerObj instanceof SingletonTargetSource) {
                Object targetObj = ((SingletonTargetSource) innerObj).getTarget();
                if (targetObj != null) {
                    objClass = targetObj.getClass();
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }
        ParameterizedType parameterizedType = findParameterizedType(objClass, onlyInterface);
        if (parameterizedType != null) {
            return (Class<T>) parameterizedType.getActualTypeArguments()[0];
        }
        throw new NullPointerException("类：" + objClass.getName() + "没有继承含有泛型参数的父类");
    }

    public static <T> Class<T> getFirstTypeArgument(Object obj) {
        return getFirstTypeArgument(obj, false);
    }

}
