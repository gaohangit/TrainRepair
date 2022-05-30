package com.ydzbinfo.emis.utils.mybatisplus.param;

import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import com.ydzbinfo.emis.utils.entity.SerializableFunction;
import com.ydzbinfo.emis.utils.mybatisplus.param.columparam.CollectionColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.columparam.SingleColumnParam;
import org.apache.ibatis.type.SimpleTypeRegistry;
import org.springframework.util.ObjectUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 张天可
 * @since 2021/12/29
 */
public class ParamUtil {

    @SafeVarargs
    public static <T> Criteria<T> allMatch(LogicalLinkable<T>... logicalLinkableArray) {
        return createCriteria(logicalLinkableArray[0].getMainClass(), LogicalLinkOperator.AND, logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> allMatch(Class<T> mainClass, LogicalLinkable<? super T>... logicalLinkableArray) {
        return createCriteria(mainClass, LogicalLinkOperator.AND, logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> anyMatch(LogicalLinkable<T>... logicalLinkableArray) {
        return createCriteria(logicalLinkableArray[0].getMainClass(), LogicalLinkOperator.OR, logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> anyMatch(Class<T> mainClass, LogicalLinkable<? super T>... logicalLinkableArray) {
        return createCriteria(mainClass, LogicalLinkOperator.OR, logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> allNotMatch(LogicalLinkable<T>... logicalLinkableArray) {
        return createCriteria(logicalLinkableArray[0].getMainClass(), LogicalLinkOperator.AND_NOT, logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> allNotMatch(Class<T> mainClass, LogicalLinkable<? super T>... logicalLinkableArray) {
        return createCriteria(mainClass, LogicalLinkOperator.AND_NOT, logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> anyNotMatch(LogicalLinkable<T>... logicalLinkableArray) {
        return createCriteria(logicalLinkableArray[0].getMainClass(), LogicalLinkOperator.OR_NOT, logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> anyNotMatch(Class<T> mainClass, LogicalLinkable<? super T>... logicalLinkableArray) {
        return createCriteria(mainClass, LogicalLinkOperator.OR_NOT, logicalLinkableArray);
    }

    /**
     * 根据实体类动态生成相等参数对象，忽略值为空字符串的值
     *
     * @param entity                 数据实体
     * @param containNullValueColumn 是否包含值为null的列，为null的列会使用"is null"sql子句
     * @param targetClass            目标类对象，用于防止把子类的属性也包括进来
     */
    @SuppressWarnings("unchecked")
    public static <T> ColumnParam<T>[] getEqColumnParamsFromEntity(T entity, boolean containNullValueColumn, Class<T> targetClass, List<SerializableFunction<T, ?>> ignoreProperties) {
        List<ColumnParam<T>> logicalLinkableList = new ArrayList<>();
        Set<String> ignoreColumns = ignoreProperties == null ? new HashSet<>() : ignoreProperties.stream().map(ParamUtil::getColumnName).collect(Collectors.toSet());
        everyColumn(entity, (String column, Function<T, Object> valueGetter) -> {
            Object value = valueGetter.apply(entity);
            Runnable doAdd = () -> {
                if (!ignoreColumns.contains(column)) {
                    logicalLinkableList.add(new SingleColumnParam<>(
                        column,
                        value,
                        ParamOperator.EQUAL_TO,
                        (Class<T>) entity.getClass(),
                        valueGetter
                    ));
                }
            };
            if (ObjectUtils.isEmpty(value)) {
                if (value == null && containNullValueColumn) {
                    doAdd.run();
                }
            } else {
                doAdd.run();
            }
        }, targetClass);
        return (ColumnParam<T>[]) logicalLinkableList.toArray(new ColumnParam<?>[0]);
    }

    public static <T> ColumnParam<T>[] getEqColumnParamsFromEntity(T entity, boolean containNullValueColumn, Class<T> targetClass) {
        return getEqColumnParamsFromEntity(entity, containNullValueColumn, targetClass, null);
    }

    public static <T> ColumnParam<T>[] getEqColumnParamsFromEntity(T entity, Class<T> targetClass, List<SerializableFunction<T, ?>> ignoreProperties) {
        return getEqColumnParamsFromEntity(entity, false, targetClass, ignoreProperties);
    }

    public static <T> ColumnParam<T>[] getEqColumnParamsFromEntity(T entity, Class<T> targetClass) {
        return getEqColumnParamsFromEntity(entity, false, targetClass);
    }

    private static <T, V> SingleColumnParam<T, V> generateSingleColumnParam(
        SerializableFunction<T, V> propertyGetter,
        V value,
        ParamOperator paramOperator
    ) {
        return new SingleColumnParam<>(
            getColumnNameAndCheckNull(propertyGetter),
            value,
            paramOperator,
            getClassByGetter(propertyGetter),
            propertyGetter
        );
    }

    private static <T, V> CollectionColumnParam<T, V> generateCollectionColumnParam(
        SerializableFunction<T, V> propertyGetter,
        Collection<V> value,
        ParamOperator paramOperator
    ) {
        return new CollectionColumnParam<>(
            getColumnNameAndCheckNull(propertyGetter),
            value,
            paramOperator,
            getClassByGetter(propertyGetter),
            propertyGetter
        );
    }

    /**
     * 获取[等于]操作的ColumnParam参数对象
     *
     * @param propertyGetter getter方法
     * @param value          参数值
     * @param <T>            数据表对应类
     * @param <V>            参数值类型
     */
    public static <T, V extends Serializable & Comparable<V>> SingleColumnParam<T, V> eqParam(SerializableFunction<T, V> propertyGetter, V value) {
        return generateSingleColumnParam(
            propertyGetter,
            value,
            ParamOperator.EQUAL_TO
        );
    }

    /**
     * 获取[不等于]操作的ColumnParam参数对象
     *
     * @param propertyGetter getter方法
     * @param value          参数值
     * @param <T>            数据表对应类
     * @param <V>            参数值类型
     */
    public static <T, V extends Serializable & Comparable<V>> SingleColumnParam<T, V> neParam(SerializableFunction<T, V> propertyGetter, V value) {
        return generateSingleColumnParam(
            propertyGetter,
            value,
            ParamOperator.NOT_EQUAL_TO
        );
    }

    /**
     * 获取[like]操作的ColumnParam参数对象
     *
     * @param <T>            数据表对应类
     * @param propertyGetter getter方法
     * @param value          参数值
     */
    public static <T> SingleColumnParam<T, String> likeParam(SerializableFunction<T, String> propertyGetter, String value) {
        return generateSingleColumnParam(
            propertyGetter,
            value,
            ParamOperator.LIKE
        );
    }

    /**
     * 获取[like]操作的ColumnParam参数对象，忽略大小写
     *
     * @param propertyGetter getter方法
     * @param value          参数值
     * @param <T>            数据表对应类
     */
    public static <T> SingleColumnParam<T, String> likeIgnoreCaseParam(SerializableFunction<T, String> propertyGetter, String value) {
        return generateSingleColumnParam(
            propertyGetter,
            value,
            ParamOperator.LIKE_IGNORE_CASE
        );
    }

    /**
     * 获取[in]操作的ColumnParam参数对象
     *
     * @param propertyGetter getter方法
     * @param value          参数值
     * @param <T>            数据表对应类
     */
    public static <T, V extends Serializable & Comparable<V>> CollectionColumnParam<T, V> inParam(SerializableFunction<T, V> propertyGetter, Collection<V> value) {
        return generateCollectionColumnParam(
            propertyGetter,
            value,
            ParamOperator.IN
        );
    }

    /**
     * 获取[not_in]操作的ColumnParam参数对象
     *
     * @param propertyGetter getter方法
     * @param value          参数值
     * @param <T>            数据表对应类
     */
    public static <T, V extends Serializable & Comparable<V>> CollectionColumnParam<T, V> notInParam(SerializableFunction<T, V> propertyGetter, Collection<V> value) {
        return generateCollectionColumnParam(
            propertyGetter,
            value,
            ParamOperator.NOT_IN
        );
    }

    /**
     * 获取[小于]操作的ColumnParam参数对象
     *
     * @param propertyGetter getter方法
     * @param value          参数值
     * @param <T>            数据表对应类
     * @param <V>            参数值类型
     */
    public static <T, V extends Serializable & Comparable<V>> SingleColumnParam<T, V> ltParam(SerializableFunction<T, V> propertyGetter, V value) {
        return generateSingleColumnParam(
            propertyGetter,
            value,
            ParamOperator.LESS_THAN
        );
    }

    /**
     * 获取[大于]操作的ColumnParam参数对象
     *
     * @param propertyGetter getter方法
     * @param value          参数值
     * @param <T>            数据表对应类
     * @param <V>            参数值类型
     */
    public static <T, V extends Serializable & Comparable<V>> SingleColumnParam<T, V> gtParam(SerializableFunction<T, V> propertyGetter, V value) {
        return generateSingleColumnParam(
            propertyGetter,
            value,
            ParamOperator.GREATER_THAN
        );
    }

    /**
     * 获取[小于等于]操作的ColumnParam参数对象
     *
     * @param propertyGetter getter方法
     * @param value          参数值
     * @param <T>            数据表对应类
     * @param <V>            参数值类型
     */
    public static <T, V extends Serializable & Comparable<V>> SingleColumnParam<T, V> leParam(SerializableFunction<T, V> propertyGetter, V value) {
        return generateSingleColumnParam(
            propertyGetter,
            value,
            ParamOperator.LESS_THAN_OR_EQUAL_TO
        );
    }

    /**
     * 获取[大于等于]操作的ColumnParam参数对象
     *
     * @param propertyGetter getter方法
     * @param value          参数值
     * @param <T>            数据表对应类
     * @param <V>            参数值类型
     */
    public static <T, V extends Serializable & Comparable<V>> SingleColumnParam<T, V> geParam(SerializableFunction<T, V> propertyGetter, V value) {
        return generateSingleColumnParam(
            propertyGetter,
            value,
            ParamOperator.GREATER_THAN_OR_EQUAL_TO
        );
    }

    /**
     * 获取[between]操作的ColumnParam参数对象
     *
     * @param propertyGetter getter方法
     * @param fromValue      参数值
     * @param toValue        参数值
     * @param <T>            数据表对应类
     */
    public static <T, V extends Serializable & Comparable<V>> CollectionColumnParam<T, V> betweenParam(SerializableFunction<T, V> propertyGetter, V fromValue, V toValue) {
        return generateCollectionColumnParam(
            propertyGetter,
            Arrays.asList(fromValue, toValue),
            ParamOperator.BETWEEN
        );
    }

    /**
     * 获取[not_between]操作的ColumnParam参数对象
     *
     * @param propertyGetter getter方法
     * @param fromValue      参数值
     * @param toValue        参数值
     * @param <T>            数据表对应类
     */
    public static <T, V extends Serializable & Comparable<V>> CollectionColumnParam<T, V> notBetweenParam(SerializableFunction<T, V> propertyGetter, V fromValue, V toValue) {
        return generateCollectionColumnParam(
            propertyGetter,
            Arrays.asList(fromValue, toValue),
            ParamOperator.NOT_BETWEEN
        );
    }

    /**
     * 获取OrderBy排序对象
     *
     * @param propertyGetter getter方法
     * @param isAsc          是否升序
     * @param <T>            数据表对应类
     */
    public static <T> OrderBy<T> orderBy(SerializableFunction<T, ?> propertyGetter, boolean isAsc) {
        return new OrderBy<>(getColumnNameAndCheckNull(propertyGetter), isAsc, getClassByGetter(propertyGetter));
    }

    static <T> Criteria<T> createCriteria(Class<T> mainClass, LogicalLinkOperator linkOperator, LogicalLinkable<? super T>[] logicalLinkableList) {
        return new Criteria<T>(
            mainClass,
            linkOperator,
            new ArrayList<>(Arrays.asList(logicalLinkableList))
        );
    }

    /**
     * 封装对数据库实体的遍历过程
     *
     * @param entity   对数据库实体实例
     * @param consumer <列名, 值> 消费者
     * @param <T>      对数据库实体类
     */
    private static <T> void everyColumn(T entity, BiConsumer<String, Function<T, Object>> consumer, Class<T> targetClass) {
        PropertyDescriptor[] propertyDescriptors = CommonUtils.getFilteredPropertyDescriptors(targetClass);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (SimpleTypeRegistry.isSimpleType(propertyDescriptor.getPropertyType())) {
                String propertyName = propertyDescriptor.getName();
                Field propertyField = ReflectUtil.getEntityField(entity.getClass(), propertyName);
                String columnName = ReflectUtil.getColumnNameFromField(propertyField);
                if (columnName != null) {
                    consumer.accept(columnName, (entity_) -> {
                        try {
                            return propertyDescriptor.getReadMethod().invoke(entity_);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    throw new RuntimeException("传入实体类的属性必须被mybatis-plus的属性注解完全注解");
                }
            }
        }
    }

    private static String getColumnNameAndCheckNull(SerializableFunction<?, ?> propertyGetter) {
        String columnName = getColumnName(propertyGetter);
        if (columnName == null) {
            throw new IllegalArgumentException("传入的getter方法对应的属性必须被TableField或TableId注解");
        }
        return columnName;
    }

    private static String getColumnName(SerializableFunction<?, ?> propertyGetter) {
        SerializedLambda serializedLambda = ReflectUtil.getSerializedLambdaFromPropertyGetter(propertyGetter);
        Class<?> entityClass = ReflectUtil.getClassBySerializedLambda(serializedLambda);
        String property = ReflectUtil.getPropertyNameByGetterNameAndClass(serializedLambda.getImplMethodName(), entityClass);
        return ReflectUtil.getColumnNameFromField(ReflectUtil.getEntityField(entityClass, property));
    }

    private static <T> Class<T> getClassByGetter(SerializableFunction<T, ?> propertyGetter) {
        SerializedLambda serializedLambda = ReflectUtil.getSerializedLambdaFromPropertyGetter(propertyGetter);
        Class<?> entityClass = ReflectUtil.getClassBySerializedLambda(serializedLambda);
        //noinspection unchecked
        return (Class<T>) entityClass;
    }
}
