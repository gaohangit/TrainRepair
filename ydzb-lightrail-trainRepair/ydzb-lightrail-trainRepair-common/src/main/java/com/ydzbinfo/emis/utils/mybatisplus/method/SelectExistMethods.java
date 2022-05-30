package com.ydzbinfo.emis.utils.mybatisplus.method;

import com.baomidou.mybatisplus.entity.Column;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import com.ydzbinfo.emis.utils.mybatisplus.param.LogicalLinkable;
import com.ydzbinfo.emis.utils.mybatisplus.wrapper.CustomWrapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author 张天可
 * @since 2021/12/29
 */
public class SelectExistMethods {

    private static <T> boolean selectExist(BaseMapper<T> mapper, CustomWrapper<T> wrapper, Class<? extends T> idEntityClass) {
        if (idEntityClass != null) {
            Column[] ids = ReflectUtil.getIdColumns(idEntityClass);
            wrapper.setSqlSelect(ids);
        }
        return mapper.selectPage(new RowBounds(0, 1), wrapper).size() > 0;
    }


    @SafeVarargs
    public static <T> boolean selectExist(BaseMapper<T> mapper, LogicalLinkable<? super T>... logicalLinkableArray) {
        Class<T> entityClass = MethodUtil.getMainClassFromBaseMapper(mapper);
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        MethodUtil.addParams(wrapper, entityClass, logicalLinkableArray);
        return selectExist(mapper, wrapper, entityClass);
    }

    public static <T> boolean selectExist(BaseMapper<T> mapper, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return selectExist(mapper, MethodUtil.getLogicalLinkableArray(logicalLinkableList));
    }

    private static <T> boolean selectExist(IService<T> service, CustomWrapper<T> wrapper, Class<? extends T> idEntityClass) {
        if (idEntityClass != null) {
            Column[] ids = ReflectUtil.getIdColumns(idEntityClass);
            wrapper.setSqlSelect(ids);
        }
        Page<T> pageInfo = new Page<>(1, 1);
        return service.selectPage(pageInfo, wrapper).getRecords().size() > 0;
    }

    @SafeVarargs
    public static <T> boolean selectExist(IService<T> service, LogicalLinkable<? super T>... logicalLinkableArray) {
        Class<T> entityClass = MethodUtil.getMainClassFromService(service);
        CustomWrapper<T> wrapper = new CustomWrapper<>();
        MethodUtil.addParams(wrapper, entityClass, logicalLinkableArray);
        return selectExist(service, wrapper, entityClass);
    }

    public static <T> boolean selectExist(IService<T> service, List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        return selectExist(service, MethodUtil.getLogicalLinkableArray(logicalLinkableList));
    }

}
