package com.ydzbinfo.emis.utils.mybatisplus;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.utils.entity.SerializableFunction;
import com.ydzbinfo.emis.utils.mybatisplus.method.*;
import com.ydzbinfo.emis.utils.mybatisplus.param.*;
import com.ydzbinfo.emis.utils.mybatisplus.param.columparam.CollectionColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.columparam.SingleColumnParam;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 * 用于封装mybatis-plus的操作，一是为了简化编码，二是防止升级后对代码改动过大
 *
 * @author 张天可
 */
@Data
public class MybatisPlusUtils {
    @SafeVarargs
    public static <T> Criteria<T> allMatch(LogicalLinkable<T>... logicalLinkableArray) {
        return ParamUtil.allMatch(logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> allMatch(Class<T> mainClass, LogicalLinkable<? super T>... logicalLinkableArray) {
        return ParamUtil.allMatch(mainClass, logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> anyMatch(LogicalLinkable<T>... logicalLinkableArray) {
        return ParamUtil.anyMatch(logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> anyMatch(Class<T> mainClass, LogicalLinkable<? super T>... logicalLinkableArray) {
        return ParamUtil.anyMatch(mainClass, logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> allNotMatch(LogicalLinkable<T>... logicalLinkableArray) {
        return ParamUtil.allNotMatch(logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> allNotMatch(Class<T> mainClass, LogicalLinkable<? super T>... logicalLinkableArray) {
        return ParamUtil.allNotMatch(mainClass, logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> anyNotMatch(LogicalLinkable<T>... logicalLinkableArray) {
        return ParamUtil.anyNotMatch(logicalLinkableArray);
    }

    @SafeVarargs
    public static <T> Criteria<T> anyNotMatch(Class<T> mainClass, LogicalLinkable<? super T>... logicalLinkableArray) {
        return ParamUtil.anyNotMatch(mainClass, logicalLinkableArray);
    }

    public static <T, V extends Serializable & Comparable<V>> SingleColumnParam<T, V> eqParam(SerializableFunction<T, V> propertyGetter, V value) {
        return ParamUtil.eqParam(propertyGetter, value);
    }

    public static <T, V extends Serializable & Comparable<V>> SingleColumnParam<T, V> neParam(SerializableFunction<T, V> propertyGetter, V value) {
        return ParamUtil.neParam(propertyGetter, value);
    }

    public static <T> SingleColumnParam<T, String> likeParam(SerializableFunction<T, String> propertyGetter, String value) {
        return ParamUtil.likeParam(propertyGetter, value);
    }

    public static <T> SingleColumnParam<T, String> likeIgnoreCaseParam(SerializableFunction<T, String> propertyGetter, String value) {
        return ParamUtil.likeIgnoreCaseParam(propertyGetter, value);
    }

    public static <T, V extends Serializable & Comparable<V>> CollectionColumnParam<T, V> inParam(SerializableFunction<T, V> propertyGetter, Collection<V> value) {
        return ParamUtil.inParam(propertyGetter, value);
    }

    public static <T, V extends Serializable & Comparable<V>> CollectionColumnParam<T, V> notInParam(SerializableFunction<T, V> propertyGetter, Collection<V> value) {
        return ParamUtil.notInParam(propertyGetter, value);
    }

    public static <T, V extends Serializable & Comparable<V>> SingleColumnParam<T, V> ltParam(SerializableFunction<T, V> propertyGetter, V value) {
        return ParamUtil.ltParam(propertyGetter, value);
    }

    public static <T, V extends Serializable & Comparable<V>> SingleColumnParam<T, V> gtParam(SerializableFunction<T, V> propertyGetter, V value) {
        return ParamUtil.gtParam(propertyGetter, value);
    }

    public static <T, V extends Serializable & Comparable<V>> SingleColumnParam<T, V> leParam(SerializableFunction<T, V> propertyGetter, V value) {
        return ParamUtil.leParam(propertyGetter, value);
    }

    public static <T, V extends Serializable & Comparable<V>> SingleColumnParam<T, V> geParam(SerializableFunction<T, V> propertyGetter, V value) {
        return ParamUtil.geParam(propertyGetter, value);
    }

    public static <T, V extends Serializable & Comparable<V>> CollectionColumnParam<T, V> betweenParam(SerializableFunction<T, V> propertyGetter, V fromValue, V toValue) {
        return ParamUtil.betweenParam(propertyGetter, fromValue, toValue);
    }

    public static <T, V extends Serializable & Comparable<V>> CollectionColumnParam<T, V> notBetweenParam(SerializableFunction<T, V> propertyGetter, V fromValue, V toValue) {
        return ParamUtil.notBetweenParam(propertyGetter, fromValue, toValue);
    }

    public static <T> OrderBy<T> orderBy(SerializableFunction<T, ?> propertyGetter, boolean isAsc) {
        return ParamUtil.orderBy(propertyGetter, isAsc);
    }

    public static <T> ColumnParam<T>[] getEqColumnParamsFromEntity(T entity, boolean containNullValueColumn, Class<T> targetClass, List<SerializableFunction<T, ?>> ignoreProperties) {
        return ParamUtil.getEqColumnParamsFromEntity(entity, containNullValueColumn, targetClass, ignoreProperties);
    }

    public static <T> ColumnParam<T>[] getEqColumnParamsFromEntity(T entity, boolean containNullValueColumn, Class<T> targetClass) {
        return ParamUtil.getEqColumnParamsFromEntity(entity, containNullValueColumn, targetClass);
    }

    public static <T> ColumnParam<T>[] getEqColumnParamsFromEntity(T entity, Class<T> targetClass, List<SerializableFunction<T, ?>> ignoreProperties) {
        return ParamUtil.getEqColumnParamsFromEntity(entity, targetClass, ignoreProperties);
    }

    public static <T> ColumnParam<T>[] getEqColumnParamsFromEntity(T entity, Class<T> targetClass) {
        return ParamUtil.getEqColumnParamsFromEntity(entity, targetClass);
    }

    public static <T> List<T> selectAll(IService<T> service) {
        return service.selectList(null);
    }

    @SafeVarargs
    public static <T> int delete(BaseMapper<T> mapper, LogicalLinkable<? super T>... logicalLinkableArray) {
        return DeleteMethods.delete(mapper, logicalLinkableArray);
    }

    public static <T> int delete(BaseMapper<T> mapper, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return DeleteMethods.delete(mapper, logicalLinkableList);
    }

    @SafeVarargs
    public static <T> boolean delete(IService<T> service, LogicalLinkable<? super T>... logicalLinkableArray) {
        return DeleteMethods.delete(service, logicalLinkableArray);
    }

    public static <T> boolean delete(IService<T> service, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return DeleteMethods.delete(service, logicalLinkableList);
    }

    @SafeVarargs
    public static <T> int update(BaseMapper<T> mapper, T entity, LogicalLinkable<? super T>... logicalLinkableArray) {
        return UpdateMethods.update(mapper, entity, logicalLinkableArray);
    }

    public static <T> int update(BaseMapper<T> mapper, T entity, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return UpdateMethods.update(mapper, entity, logicalLinkableList);
    }

    @SafeVarargs
    public static <T> boolean update(IService<T> service, T entity, LogicalLinkable<? super T>... logicalLinkableArray) {
        return UpdateMethods.update(service, entity, logicalLinkableArray);
    }

    public static <T> boolean update(IService<T> service, T entity, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return UpdateMethods.update(service, entity, logicalLinkableList);
    }

    @SafeVarargs
    public static <T> List<T> selectList(BaseMapper<T> mapper, LogicalLinkable<? super T>... logicalLinkableArray) {
        return SelectListMethods.selectList(mapper, logicalLinkableArray);
    }

    public static <T> List<T> selectList(BaseMapper<T> mapper, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return SelectListMethods.selectList(mapper, logicalLinkableList);
    }

    public static <T> List<T> selectList(BaseMapper<T> mapper, List<? extends LogicalLinkable<? super T>> logicalLinkableList, List<? extends OrderBy<? super T>> orderByList) {
        return SelectListMethods.selectList(mapper, logicalLinkableList, orderByList);
    }


    @SafeVarargs
    public static <T> List<T> selectList(IService<T> service, LogicalLinkable<? super T>... logicalLinkableArray) {
        return SelectListMethods.selectList(service, logicalLinkableArray);
    }

    public static <T> List<T> selectList(IService<T> service, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return SelectListMethods.selectList(service, logicalLinkableList);
    }

    public static <T> List<T> selectList(IService<T> service, List<? extends LogicalLinkable<? super T>> logicalLinkableList, List<? extends OrderBy<? super T>> orderByList) {
        return SelectListMethods.selectList(service, logicalLinkableList, orderByList);
    }

    @SafeVarargs
    public static <T> boolean selectExist(BaseMapper<T> mapper, LogicalLinkable<? super T>... logicalLinkableArray) {
        return SelectExistMethods.selectExist(mapper, logicalLinkableArray);
    }

    public static <T> boolean selectExist(BaseMapper<T> mapper, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return SelectExistMethods.selectExist(mapper, logicalLinkableList);
    }

    @SafeVarargs
    public static <T> boolean selectExist(IService<T> service, LogicalLinkable<? super T>... logicalLinkableArray) {
        return SelectExistMethods.selectExist(service, logicalLinkableArray);
    }

    public static <T> boolean selectExist(IService<T> service, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return SelectExistMethods.selectExist(service, logicalLinkableList);
    }

    @SafeVarargs
    public static <T> int selectCount(BaseMapper<T> mapper, LogicalLinkable<? super T>... logicalLinkableArray) {
        return SelectCountMethods.selectCount(mapper, logicalLinkableArray);
    }

    public static <T> int selectCount(BaseMapper<T> mapper, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return SelectCountMethods.selectCount(mapper, logicalLinkableList);
    }

    @SafeVarargs
    public static <T> int selectCount(IService<T> service, LogicalLinkable<? super T>... logicalLinkableArray) {
        return SelectCountMethods.selectCount(service, logicalLinkableArray);
    }

    public static <T> int selectCount(IService<T> service, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return SelectCountMethods.selectCount(service, logicalLinkableList);
    }

    @SafeVarargs
    public static <T> Page<T> selectPage(IService<T> service, int current, int size, LogicalLinkable<? super T>... logicalLinkableArray) {
        return SelectPageMethods.selectPage(service, current, size, logicalLinkableArray);
    }

    public static <T> Page<T> selectPage(IService<T> service, int current, int size, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return SelectPageMethods.selectPage(service, current, size, logicalLinkableList);
    }

    public static <T> Page<T> selectPage(IService<T> service, int current, int size, List<? extends LogicalLinkable<? super T>> logicalLinkableList, List<? extends OrderBy<? super T>> orderByList) {
        return SelectPageMethods.selectPage(service, current, size, logicalLinkableList, orderByList);
    }

}
