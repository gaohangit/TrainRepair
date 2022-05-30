package com.ydzbinfo.emis.utils.mybatisplus.param.columparam;

import com.ydzbinfo.emis.utils.mybatisplus.param.ParamOperator;
import lombok.Getter;

import java.util.function.Function;

/**
 * @author 张天可
 * @since 2021/12/7
 */
@Getter
abstract class AbstractColumnParam<T, V, P> implements GenericColumnParam<T, V, P> {
    private final String columnName;
    private final P paramValue;
    private final ParamOperator operator;
    private final Class<T> mainClass;
    private final Function<T, V> propertyGetter;

    AbstractColumnParam(String columnName, P paramValue, ParamOperator operator, Class<T> mainClass, Function<T, V> propertyGetter) {
        this.columnName = columnName;
        this.paramValue = paramValue;
        this.operator = operator;
        this.mainClass = mainClass;
        this.propertyGetter = propertyGetter;
    }

    @Override
    public boolean test(T entity) {
        return operator.test(propertyGetter.apply(entity), paramValue);
    }

}
