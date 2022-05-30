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
public class SelectCountMethods {


    @SafeVarargs
    public static <T> int selectCount(BaseMapper<T> mapper, LogicalLinkable<? super T>... logicalLinkableArray) {
        Class<T> entityClass = MethodUtil.getMainClassFromBaseMapper(mapper);
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        MethodUtil.addParams(wrapper, entityClass, logicalLinkableArray);
        return mapper.selectCount(wrapper);
    }

    public static <T> int selectCount(BaseMapper<T> mapper, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return selectCount(mapper, MethodUtil.getLogicalLinkableArray(logicalLinkableList));
    }

    @SafeVarargs
    public static <T> int selectCount(IService<T> service, LogicalLinkable<? super T>... logicalLinkableArray) {
        Class<T> entityClass = MethodUtil.getMainClassFromService(service);
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        MethodUtil.addParams(wrapper, entityClass, logicalLinkableArray);
        return service.selectCount(wrapper);
    }

    public static <T> int selectCount(IService<T> service, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return selectCount(service, MethodUtil.getLogicalLinkableArray(logicalLinkableList));
    }

}
