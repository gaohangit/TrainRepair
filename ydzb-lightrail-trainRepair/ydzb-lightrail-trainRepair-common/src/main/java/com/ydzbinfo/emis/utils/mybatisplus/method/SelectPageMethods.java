package com.ydzbinfo.emis.utils.mybatisplus.method;

import com.baomidou.mybatisplus.plugins.Page;
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
public class SelectPageMethods {


    @SafeVarargs
    public static <T> Page<T> selectPage(IService<T> service, int current, int size, LogicalLinkable<? super T>... logicalLinkableArray) {
        Class<T> entityClass = MethodUtil.getMainClassFromService(service);
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        MethodUtil.addParams(wrapper, entityClass, logicalLinkableArray);
        return service.selectPage(new Page<>(current, size), wrapper);
    }

    public static <T> Page<T> selectPage(IService<T> service, int current, int size, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return selectPage(service, current, size, logicalLinkableList, null);
    }

    public static <T> Page<T> selectPage(IService<T> service, int current, int size, List<? extends LogicalLinkable<? super T>> logicalLinkableList, List<? extends OrderBy<? super T>> orderByList) {
        Class<T> entityClass = MethodUtil.getMainClassFromService(service);
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        MethodUtil.addParams(wrapper, entityClass, MethodUtil.getLogicalLinkableArray(logicalLinkableList));
        MethodUtil.orderBy(wrapper, MethodUtil.getOrderByArray(orderByList));
        return service.selectPage(new Page<>(current, size), wrapper);
    }
}
