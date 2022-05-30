package com.ydzbinfo.emis.utils.mybatisplus.param.columparam;

import com.ydzbinfo.emis.utils.mybatisplus.param.ParamOperator;
import lombok.Getter;

import java.util.Collection;
import java.util.function.Function;

/**
 * 列表值条件参数
 *
 * @author 张天可
 * @since 2022/4/22
 */
@Getter
public class CollectionColumnParam<T, V> extends AbstractColumnParam<T, V, Collection<V>> {
    public CollectionColumnParam(String columnName, Collection<V> value, ParamOperator operator, Class<T> tClass, Function<T, V> propertyGetter) {
        super(columnName, value, operator, tClass, propertyGetter);
        if (operator.isSingle()) {
            throw new RuntimeException("CollectionColumnParam只支持isSingle为false的Operator");
        }
    }
}
