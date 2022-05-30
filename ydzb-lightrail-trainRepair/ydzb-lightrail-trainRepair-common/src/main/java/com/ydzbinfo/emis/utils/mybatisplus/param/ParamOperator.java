package com.ydzbinfo.emis.utils.mybatisplus.param;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ydzbinfo.emis.utils.TriConsumer;
import com.ydzbinfo.emis.utils.mybatisplus.wrapper.CustomWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * 条件运算符
 *
 * @author 张天可
 * @since 2021/12/7
 */
@Getter
@AllArgsConstructor
public enum ParamOperator {
    EQUAL_TO(true, (wrapper, column, value) -> {
        if (value == null) {
            wrapper.isNull(column);
        } else {
            wrapper.eq(column, value);
        }
    }, Object::equals),
    NOT_EQUAL_TO(true, (wrapper, column, value) -> {
        if (value == null) {
            wrapper.isNotNull(column);
        } else {
            wrapper.ne(column, value);
        }
    }, (Object value, Object paramValue) -> !Objects.equals(value, paramValue)),
    LIKE(true, (wrapper, column, value) -> {
        wrapper.like(column, (String) Objects.requireNonNull(value, () -> "列：" + column + "，进行LIKE操作时值不能为null。"));
    }, null),
    LIKE_IGNORE_CASE(true, (wrapper, column, value) -> {
        wrapper.likeIgnoreCase(column, (String) Objects.requireNonNull(value, () -> "列：" + column + "，进行LIKE操作时值不能为null。"));
    }, null),
    LESS_THAN(true, Wrapper::lt, null),
    GREATER_THAN(true, Wrapper::gt, null),
    LESS_THAN_OR_EQUAL_TO(true, Wrapper::le, null),
    GREATER_THAN_OR_EQUAL_TO(true, Wrapper::ge, null),
    BETWEEN(false, (wrapper, column, value) -> {
        List<?> listValue = (List<?>) value;
        wrapper.between(column, listValue.get(0), listValue.get(1));
    }, null),
    NOT_BETWEEN(false, (wrapper, column, value) -> {
        List<?> listValue = (List<?>) value;
        wrapper.notBetween(column, listValue.get(0), listValue.get(1));
    }, null),
    NOT_IN(false, (wrapper, column, value) -> {
        Collection<?> collectionValue = (Collection<?>) value;
        if (CollectionUtils.isEmpty(collectionValue)) {
            throw new IllegalArgumentException("列：" + column + "，NOT_IN操作的值列表不能为空");
        }
        wrapper.notIn(column, collectionValue);
    }, null),
    IN(false, (wrapper, column, value) -> {
        Collection<?> collectionValue = (Collection<?>) value;
        if (CollectionUtils.isEmpty(collectionValue)) {
            throw new IllegalArgumentException("列：" + column + "，IN操作的值列表不能为空");
        }
        wrapper.in(column, collectionValue);
    }, null);
    private final boolean isSingle;
    private final TriConsumer<CustomWrapper<?>, String, Object> setValueToWrapper;
    private final BiPredicate<Object, Object> valueTester;

    public void setValueToWrapper(CustomWrapper<?> wrapper, String column, Object value) {
        setValueToWrapper.accept(wrapper, column, value);
    }

    public boolean test(Object value, Object paramValue) {
        if (valueTester == null) {
            throw new RuntimeException(this + "暂不支持java对象匹配");
        }
        return valueTester.test(value, paramValue);
    }
}
