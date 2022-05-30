package com.ydzbinfo.emis.utils;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import com.ydzbinfo.hussar.core.util.SpringCtxHolder;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author 张天可
 * @description 基本工具集
 * @date 2020/11/30
 **/
public class CommonUtils {

    /**
     * 在列表里根据不同优先级key值查找元素
     *
     * @param itemList
     * @param getKey
     * @param keys     key值列表，下标小优先级大
     * @param <T>
     * @param <K>
     * @return
     */
    @SafeVarargs
    public static <T, K> T findItemFromList(List<T> itemList, Function<T, K> getKey, K... keys) {
        Set<K> kSet = new HashSet<>(Arrays.asList(keys));
        Map<K, T> findResultMap = new HashMap<>();
        for (T item : itemList) {
            K curItemKey = getKey.apply(item);
            if (kSet.contains(curItemKey) && !findResultMap.containsKey(curItemKey)) {
                findResultMap.put(curItemKey, item);
            }
        }

        for (K key : keys) {
            if (findResultMap.containsKey(key)) {
                return findResultMap.get(key);
            }
        }

        return null;
    }

    public static <T, K> List<T> getDistinctList(List<T> source, Function<T, K> getKey) {
        LinkedHashMap<K, T> target = new LinkedHashMap<>();
        for (T item : source) {
            K itemKey = getKey.apply(item);
            if (!target.containsKey(itemKey)) {
                target.put(itemKey, item);
            }
        }
        return new ArrayList<>(target.values());
    }

    public static <T> boolean every(Iterable<T> list, Predicate<T> predicate) {
        for (T item : list) {
            if (!predicate.test(item)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean every(T[] list, Predicate<T> predicate) {
        for (T item : list) {
            if (!predicate.test(item)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean some(Iterable<T> list, Predicate<T> predicate) {
        return !every(list, v -> !predicate.test(v));
    }

    public static <T> boolean some(T[] list, Predicate<T> predicate) {
        return !every(list, v -> !predicate.test(v));
    }

    public static <T> T find(Iterable<T> iterable, Predicate<T> predicate) {
        for (T item : iterable) {
            if (predicate.test(item)) {
                return item;
            }
        }
        return null;
    }

    public static <T> T find(T[] array, Predicate<T> predicate) {
        for (T item : array) {
            if (predicate.test(item)) {
                return item;
            }
        }
        return null;
    }

    public static <T> int findIndex(List<T> list, Predicate<T> predicate) {
        int index = 0;
        for (T item : list) {
            if (predicate.test(item)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public static <T> int findIndex(T[] array, Predicate<T> predicate) {
        int index = 0;
        for (T item : array) {
            if (predicate.test(item)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public static <T, U, V> Map<U, V> reduceMap(Stream<T> stream, BiFunction<Map<U, V>, T, Map<U, V>> accumulator) {
        return stream.reduce(new HashMap<>(), accumulator, (a, b) -> {
            if (a != b) {
                a.putAll(b);
            }
            return a;
        });
    }

    public static <T, U> Map<U, T> collectionToMap(Collection<T> collection, Function<T, U> getKey) {
        return collectionToMap(collection.stream(), getKey);
    }

    public static <T, U> Map<U, T> collectionToMap(Stream<T> stream, Function<T, U> getKey) {
        return reduceMap(stream, (map, value) -> {
            map.put(getKey.apply(value), value);
            return map;
        });
    }

    /**
     * 获取反向流
     */
    public static <T> Stream<T> reverseStreamOf(T[] array) {
        if (array == null) {
            return null;
        } else {
            return IntStream.rangeClosed(1, array.length).mapToObj(i -> array[array.length - i]);
        }
    }

    /**
     * 获取反向流
     */
    public static <T> Stream<T> reverseStreamOf(List<T> list) {
        if (list == null) {
            return null;
        } else {
            return IntStream.rangeClosed(1, list.size()).mapToObj(i -> list.get(list.size() - i));
        }
    }

    /**
     * 获取实体类的属性，排除了Class类型
     *
     * @param entityClass
     * @return
     */
    public static PropertyDescriptor[] getFilteredPropertyDescriptors(Class<?> entityClass) {
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(entityClass);
        return Arrays.stream(propertyDescriptors).filter(v -> {
            Class<?> propertyType = v.getPropertyType();
            return !propertyType.equals(Class.class);
        }).toArray(PropertyDescriptor[]::new);
    }


    /**
     * 根据属性将属性值分割到不同的实体类
     * <不建议使用>
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> T[] splitEntitiesByProperties(T entity, String[]... propertyGroups) {
        Class<T> entityClass = (Class<T>) entity.getClass();
        T[] splitEntities = (T[]) Array.newInstance(entityClass, propertyGroups.length + 1);
        Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<>();
        PropertyDescriptor[] pds = CommonUtils.getFilteredPropertyDescriptors(entityClass);
        for (PropertyDescriptor pd : pds) {
            propertyDescriptorMap.put(pd.getName(), pd);
        }
        Set<String> propertiesCache = new HashSet<>();

        for (int i = 0; i < splitEntities.length; i++) {
            try {
                splitEntities[i] = entityClass.newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
                throw new RuntimeException("数据对象初始化失败", e);
            }
            T curEntity = splitEntities[i];
            if (i < propertyGroups.length) {
                String[] propertyGroup = propertyGroups[i];
                for (String property : propertyGroup) {
                    if (propertyDescriptorMap.containsKey(property)) {
                        if (!propertiesCache.contains(property)) {
                            propertiesCache.add(property);
                            Object value = ReflectUtil.getValue(entity, property);
                            ReflectUtil.setValue(curEntity, property, value);
                        } else {
                            throw new RuntimeException("输入属性存在重复: " + property);
                        }
                    } else {
                        throw new RuntimeException("对象" + entity + "不存在属性：" + property);
                    }
                }
            } else {// 收集剩余的属性
                propertyDescriptorMap.forEach((property, pd) -> {
                    if (!propertiesCache.contains(property)) {
                        Object value = ReflectUtil.getValue(entity, property);
                        ReflectUtil.setValue(curEntity, property, value);
                    }
                });
            }
        }
        return splitEntities;
    }


    /**
     * 将既有分页数据转换为分页对象
     *
     * @param data
     * @param total
     * @param pageNum
     * @param <T>
     * @return
     */
    public static <T> Page<T> convertToPage(List<T> data, int total, int pageNum, int pageSize) {
        Page<T> page = new Page<>(pageNum, pageSize);
        page.setRecords(data);
        page.setTotal(total);
        return page;
    }

    public static int getLastPageSize(int total, int pageSize) {
        if (total == 0) {
            return 0;
        } else {
            int value = total % pageSize;
            return value == 0 ? pageSize : value;
        }
    }

    /**
     * 根据已有全部数据，提取分页对象
     *
     * @param data
     * @param pageNum
     * @param pageSize
     * @param <T>
     * @return
     */
    public static <T> Page<T> getPage(List<T> data, int pageNum, int pageSize) {
        int total = data.size();
        if (total == 0) {
            Page<T> page = new Page<>();
            page.setSize(0);
            page.setCurrent(1);
            page.setRecords(new ArrayList<>(data));
            page.setTotal(total);
            return page;
        }
        int lastPageSize = getLastPageSize(total, pageSize);
        int pageCount = total / pageSize + (lastPageSize == pageSize ? 0 : 1);
        // 合理化页码
        if (pageNum > pageCount) {
            pageNum = pageCount;
        } else if (pageNum <= 0) {
            pageNum = 1;
        }
        int realPageSize = pageNum == pageCount ? lastPageSize : pageSize;
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = fromIndex + realPageSize;
        List<T> pageData = data.subList(fromIndex, toIndex);
        Page<T> page = new Page<>(pageNum, pageSize);
        page.setRecords(pageData);
        page.setTotal(total);
        return page;
    }

    /**
     * 根据当前对象获取对应的spring bean对象，仅支持单例模式
     *
     * @param thisObj
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBeanByThis(T thisObj) {
        Map<String, T> beansOfTypeMap = SpringCtxHolder.getApplicationContext().getBeansOfType((Class<T>) thisObj.getClass());
        if (beansOfTypeMap.size() > 0) {
            if (beansOfTypeMap.size() == 1) {
                return (T) beansOfTypeMap.values().toArray()[0];
            }
            for (T targetObj : beansOfTypeMap.values()) {
                if (targetObj == thisObj) {
                    return targetObj;
                }
                try {
                    Method getTargetSourceMethod = targetObj.getClass().getMethod("getTargetSource");
                    try {
                        Object innerObj = getTargetSourceMethod.invoke(targetObj);
                        if (innerObj instanceof SingletonTargetSource && Objects.equals(((SingletonTargetSource) innerObj).getTarget(), thisObj)) {
                            return targetObj;
                        }
                    } catch (IllegalAccessException | InvocationTargetException ignored) {
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }
        }
        throw new IllegalArgumentException("参数错误，未找到bean对象");
    }

    /**
     * 校验类型的属性复制
     */
    public static <T> void copyPropertiesToOther(T source, T target, Class<T> tClass) {
        Objects.requireNonNull(tClass);
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 校验父子关系的属性复制
     */
    public static <S, T extends S> void copyPropertiesToChild(
        S source, Class<S> sourceClass,
        T target, Class<T> targetClass
    ) {
        Objects.requireNonNull(sourceClass);
        Objects.requireNonNull(targetClass);
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 校验父子关系的属性复制
     */
    public static <S extends T, T> void copyPropertiesToParent(
        S source, Class<S> sourceClass,
        T target, Class<T> targetClass
    ) {
        Objects.requireNonNull(sourceClass);
        Objects.requireNonNull(targetClass);
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 将实体类里的空字符串转换为null
     */
    public static void emptyStringToNull(Object entity) {
        try {
            PropertyDescriptor[] propertyDescriptors = CommonUtils.getFilteredPropertyDescriptors(entity.getClass());
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if (propertyDescriptor.getPropertyType().equals(String.class)) {
                    if ("".equals(propertyDescriptor.getReadMethod().invoke(entity))) {
                        Object[] objects = new Object[]{null};
                        propertyDescriptor.getWriteMethod().invoke(entity, objects);
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("emptyStringToNull调用失败", e);
        }
    }

    /**
     * 转换至泛型数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list, TypeReference<T> typeReference) {
        T[] emptyArray = (T[]) Array.newInstance(
            getClassFromTypeReference(typeReference),
            0
        );
        if (list != null) {
            return list.toArray(emptyArray);
        } else {
            return emptyArray;
        }
    }


    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassFromTypeReference(TypeReference<T> typeReference) {
        Type type = typeReference.getType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<T>) parameterizedType.getRawType();
        } else if (type instanceof Class) {
            return (Class<T>) type;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 扁平化树形数据
     *
     * @param treeNodeList   原始树形数据
     * @param childrenGetter 子节点获取方法
     * @param toT            将单个树形节点转换为单个最终结果
     * @param <T>            最终结果类型
     * @param <TREE_NODE>    树形节点类型
     */
    public static <T, TREE_NODE> List<T> flatTree(List<TREE_NODE> treeNodeList, Function<TREE_NODE, List<TREE_NODE>> childrenGetter, Function<TREE_NODE, T> toT) {
        List<T> listB = new ArrayList<>();
        for (TREE_NODE treeNode : treeNodeList) {
            listB.add(toT.apply(treeNode));
            List<TREE_NODE> children = childrenGetter.apply(treeNode);
            List<T> subListB = flatTree(children, childrenGetter, toT);
            listB.addAll(subListB);
        }
        return listB;
    }
}
