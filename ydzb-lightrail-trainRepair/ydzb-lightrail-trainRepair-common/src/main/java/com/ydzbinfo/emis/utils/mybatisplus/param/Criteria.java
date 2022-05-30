package com.ydzbinfo.emis.utils.mybatisplus.param;

import lombok.Getter;

import java.util.List;
import java.util.function.Predicate;

/**
 * 由多个自条件和一个连接符构成的聚合条件
 *
 * @author 张天可
 * @since 2021/12/24
 */
@Getter
public class Criteria<T> implements LogicalLinkable<T> {
    private final Class<T> mainClass;
    private final LogicalLinkOperator linkOperator;
    private final List<LogicalLinkable<? super T>> children;

    Criteria(Class<T> mainClass, LogicalLinkOperator linkOperator, List<LogicalLinkable<? super T>> children) {
        assert mainClass != null;
        assert linkOperator != null;
        assert children != null;
        this.linkOperator = linkOperator;
        this.mainClass = mainClass;
        this.children = children;
    }

    @Override
    public Class<T> getMainClass() {
        return mainClass;
    }

    @Override
    public boolean test(T entity) {
        return linkOperator.combine(children.stream(), v -> v.test(entity));
    }

}
