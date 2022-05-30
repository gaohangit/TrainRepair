package com.ydzbinfo.emis.utils.mybatisplus.param.columparam;

import com.ydzbinfo.emis.utils.mybatisplus.param.ParamOperator;
import lombok.Getter;

import java.util.function.Function;

/**
 * 单个值列条件参数
 *
 * @author 张天可
 * @since 2022/4/22
 */
@Getter
public class SingleColumnParam<T, V> extends AbstractColumnParam<T, V, V> {
    public SingleColumnParam(String columnName, V value, ParamOperator operator, Class<T> tClass, Function<T, V> propertyGetter) {
        super(columnName, value, operator, tClass, propertyGetter);
        if (!operator.isSingle()) {
            throw new RuntimeException("SingleColumnParam只支持isSingle为true的Operator");
        }
    }
}
