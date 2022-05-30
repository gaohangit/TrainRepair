package com.ydzbinfo.emis.utils.mybatisplus.method;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.utils.mybatisplus.param.LogicalLinkable;
import com.ydzbinfo.emis.utils.mybatisplus.wrapper.CustomWrapper;

import java.util.List;

/**
 * @author 张天可
 * @since 2021/12/29
 */
public class UpdateMethods {

    @SafeVarargs
    public static <T> int update(BaseMapper<T> mapper, T entity, LogicalLinkable<? super T>... logicalLinkableArray) {
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        //noinspection unchecked
        MethodUtil.addParams(wrapper, (Class<T>) entity.getClass(), logicalLinkableArray);
        return mapper.update(entity, wrapper);
    }

    public static <T> int update(BaseMapper<T> mapper, T entity, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return update(mapper, entity, MethodUtil.getLogicalLinkableArray(logicalLinkableList));
    }

    @SafeVarargs
    public static <T> boolean update(IService<T> service, T entity, LogicalLinkable<? super T>... logicalLinkableArray) {
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        //noinspection unchecked
        MethodUtil.addParams(wrapper, (Class<T>) entity.getClass(), logicalLinkableArray);
        return service.update(entity, wrapper);
    }

    public static <T> boolean update(IService<T> service, T entity, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return update(service, entity, MethodUtil.getLogicalLinkableArray(logicalLinkableList));
    }

}
