package com.ydzbinfo.emis.utils;

import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import com.ydzbinfo.emis.utils.entity.SerializableFunction;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.*;

/**
 * mybatis ognl工具类
 *
 * @author 张天可
 */
public class MybatisOgnlUtils {

    protected static final Logger logger = LoggerFactory.getLogger(MybatisOgnlUtils.class);

    /**
     * 仅支持相等与like操作
     *
     * @author 张天可
     */
    @Data
    public static class SimpleCondition {
        String columnName;
        String operator;
        Object value;
        String extraTail;
    }

    public static List<SimpleCondition> getSimpleConditions(Object entity, String likeProperties) {
        List<SimpleCondition> simpleConditions = new ArrayList<>();
        if (entity != null) {
            Class<?> entityClass = entity.getClass();
            Set<String> likePropertySet;
            likePropertySet = new HashSet<>(Arrays.asList(likeProperties.split(",")));
            for (PropertyDescriptor propertyDescriptor : CommonUtils.getFilteredPropertyDescriptors(entityClass)) {
                String property = propertyDescriptor.getName();
                String columnName = ReflectUtil.getColumnNameFromField(ReflectUtil.getEntityField(entityClass, property));
                Object value = ReflectUtil.getValue(entity, property);
                if (!StringUtils.isEmpty(columnName) && value != null && !Objects.equals(value, "")) {
                    SimpleCondition simpleCondition = new SimpleCondition();
                    simpleCondition.setColumnName(columnName);
                    boolean isLike = likePropertySet.contains(property);
                    simpleCondition.setOperator(isLike ? "like" : "=");
                    simpleCondition.setValue(isLike ? "%" + replaceWildcardChars(value.toString()) + "%" : value);
                    simpleCondition.setExtraTail(isLike ? " escape '\\'" : "");
                    simpleConditions.add(simpleCondition);
                }
            }
        }
        return simpleConditions;
    }

    /**
     * 为了修复超大量in sql
     *
     * @param values
     * @return
     */
    public static <T> List<List<T>> splitValuesForHugeSizeIn(Iterable<T> values) {
        List<List<T>> orValueList = new ArrayList<>();
        int maxLength = 1000;
        int i = 0;

        List<T> objects = new ArrayList<>();
        for (T value : values) {
            objects.add(i, value);
            if (i == maxLength - 1) {
                i = 0;
                orValueList.add(objects);
                objects = new ArrayList<>();
            } else {
                i++;
            }
        }
        if (i != 0) {
            orValueList.add(objects);
        }
        return orValueList;
    }

    public static <T> List<List<T>> splitValuesForHugeSizeIn(T[] values) {
        return splitValuesForHugeSizeIn(Arrays.asList(values));
    }

    private static final Set<Character> oracleWildcardChars = new HashSet<>(Arrays.asList('%', '_'));

    public static final char DEFAULT_LIKE_ESCAPE_CHAR = '\\';

    /**
     * 转义sql like 使用的特殊字符
     * 建议与拼接%的逻辑在同一处代码
     *
     * @param str        原始字符串
     * @param escapeChar 转义字符
     * @return 转义后字符
     */
    public static String replaceWildcardChars(String str, char escapeChar) {
        if (str == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (oracleWildcardChars.contains(c) || escapeChar == c) {
                stringBuilder.append(escapeChar);
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public static String replaceWildcardChars(String str) {
        return replaceWildcardChars(str, DEFAULT_LIKE_ESCAPE_CHAR);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getNewEntityReplacedWildcardChars(T entity, char escapeChar, SerializableFunction<T, String>... propertyGetters) {
        try {
            T newEntity = (T) entity.getClass().newInstance();
            String[] properties = Arrays.stream(propertyGetters).map(
                propertyGetter -> ReflectUtil.getPropertyNameByGetter(propertyGetter, entity.getClass(), () -> new IllegalArgumentException("请传入当前实体类的getter方法引用"))
            ).toArray(String[]::new);
            BeanUtils.copyProperties(entity, newEntity, properties);
            for (int i = 0; i < propertyGetters.length; i++) {
                SerializableFunction<T, String> propertyGetter = propertyGetters[i];
                String value = propertyGetter.apply(entity);
                if (value != null) {
                    String property = properties[i];
                    ReflectUtil.setValue(newEntity, property, replaceWildcardChars(value, escapeChar));
                }
            }
            return newEntity;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("转义替换失败", e);
        }
    }

    /**
     * 复制一份对象，将指定的属性(以getter方法的形式)中的like通配符进行转义
     */
    @SafeVarargs
    public static <T> T getNewEntityReplacedWildcardChars(T entity, SerializableFunction<T, String>... propertyGetters) {
        return getNewEntityReplacedWildcardChars(entity, DEFAULT_LIKE_ESCAPE_CHAR, propertyGetters);
    }
}
