package com.ydzbinfo.emis.utils.mybatisplus.param.columparam;

import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;

import java.util.function.Function;

/**
 * 针对参数值和值构造泛型了的列条件参数
 *
 * @author 张天可
 * @since 2022/4/22
 */
public interface GenericColumnParam<T, V, P> extends ColumnParam<T> {
    P getParamValue();

    Function<T, V> getPropertyGetter();
}
