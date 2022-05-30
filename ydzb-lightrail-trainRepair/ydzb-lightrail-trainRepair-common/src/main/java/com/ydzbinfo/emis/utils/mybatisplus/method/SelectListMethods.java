package com.ydzbinfo.emis.utils.mybatisplus.method;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import com.ydzbinfo.emis.utils.mybatisplus.param.LogicalLinkable;
import com.ydzbinfo.emis.utils.mybatisplus.param.OrderBy;
import com.ydzbinfo.emis.utils.mybatisplus.wrapper.CustomWrapper;

import java.util.List;

/**
 * @author 张天可
 * @since 2021/12/29
 */
public class SelectListMethods {

    /**
     * 根据ColumnParam、Criteria参数对象列表查询数据库
     *
     * @param logicalLinkableArray 通过eqParam、likeParam或inParam等方法生成
     */
    @SafeVarargs
    public static <T> List<T> selectList(BaseMapper<T> mapper, LogicalLinkable<? super T>... logicalLinkableArray) {
        Class<T> entityClass = MethodUtil.getMainClassFromBaseMapper(mapper);
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        MethodUtil.addParams(wrapper, entityClass, logicalLinkableArray);
        return mapper.selectList(wrapper);
    }

    /**
     * 根据参数条件查询
     */
    public static <T> List<T> selectList(BaseMapper<T> mapper, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return selectList(mapper, logicalLinkableList, null);
    }

    /**
     * 根据参数条件和排序条件查询
     */
    public static <T> List<T> selectList(BaseMapper<T> mapper, List<? extends LogicalLinkable<? super T>> logicalLinkableList, List<? extends OrderBy<? super T>> orderByList) {
        Class<T> entityClass = MethodUtil.getMainClassFromBaseMapper(mapper);
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        MethodUtil.addParams(wrapper, entityClass, MethodUtil.getLogicalLinkableArray(logicalLinkableList));
        MethodUtil.orderBy(wrapper, MethodUtil.getOrderByArray(orderByList));
        return mapper.selectList(wrapper);
    }


    /**
     * 根据ColumnParam、Criteria参数对象列表查询数据库
     *
     * @param logicalLinkableArray 通过eqParam、likeParam或inParam等方法生成
     */
    @SafeVarargs
    public static <T> List<T> selectList(IService<T> service, LogicalLinkable<? super T>... logicalLinkableArray) {
        Class<T> entityClass = MethodUtil.getMainClassFromService(service);
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        MethodUtil.addParams(wrapper, entityClass, logicalLinkableArray);
        return service.selectList(wrapper);
    }

    /**
     * 根据参数条件查询
     */
    public static <T> List<T> selectList(IService<T> service, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return selectList(service, logicalLinkableList, null);
    }

    /**
     * 根据参数条件和排序条件查询
     */
    public static <T> List<T> selectList(IService<T> service, List<? extends LogicalLinkable<? super T>> logicalLinkableList, List<? extends OrderBy<? super T>> orderByList) {
        Class<T> entityClass = MethodUtil.getMainClassFromService(service);
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        MethodUtil.addParams(wrapper, entityClass, MethodUtil.getLogicalLinkableArray(logicalLinkableList));
        MethodUtil.orderBy(wrapper, MethodUtil.getOrderByArray(orderByList));
        return service.selectList(wrapper);
    }
}
