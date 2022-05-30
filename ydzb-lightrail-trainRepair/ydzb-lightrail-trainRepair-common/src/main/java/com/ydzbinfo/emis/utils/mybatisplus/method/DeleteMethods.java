package com.ydzbinfo.emis.utils.mybatisplus.method;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import com.ydzbinfo.emis.utils.mybatisplus.param.LogicalLinkable;
import com.ydzbinfo.emis.utils.mybatisplus.wrapper.CustomWrapper;

import java.util.List;

/**
 * @author 张天可
 * @since 2021/12/29
 */
public class DeleteMethods {

    @SafeVarargs
    public static <T> int delete(BaseMapper<T> mapper, LogicalLinkable<? super T>... logicalLinkableArray) {
        Class<T> entityClass = MethodUtil.getMainClassFromBaseMapper(mapper);
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        MethodUtil.addParams(wrapper, entityClass, logicalLinkableArray);
        return mapper.delete(wrapper);
    }

    public static <T> int delete(BaseMapper<T> mapper, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return delete(mapper, MethodUtil.getLogicalLinkableArray(logicalLinkableList));
    }

    @SafeVarargs
    public static <T> boolean delete(IService<T> service, LogicalLinkable<? super T>... logicalLinkableArray) {
        Class<T> entityClass = MethodUtil.getMainClassFromService(service);
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        MethodUtil.addParams(wrapper, entityClass, logicalLinkableArray);
        return service.delete(wrapper);
    }

    public static <T> boolean delete(IService<T> service, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return delete(service, MethodUtil.getLogicalLinkableArray(logicalLinkableList));
    }
}
