package com.ydzbinfo.emis.utils.mybatisplus.param;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 逻辑运算符
 *
 * @author 张天可
 * @since 2021/12/14
 */
@Getter
@AllArgsConstructor
public enum LogicalLinkOperator {
    AND(LogicalLinkOperator::andCombine),
    OR(LogicalLinkOperator::orCombine),
    AND_NOT(LogicalLinkOperator::andNotCombine),
    OR_NOT(LogicalLinkOperator::orNotCombine);
    private final LogicCombiner logicCombiner;

    @FunctionalInterface
    private interface LogicCombiner {
        <E> boolean apply(Stream<E> stream, Predicate<E> tester);
    }

    public <E> boolean combine(Stream<E> stream, Predicate<E> tester) {
        return logicCombiner.apply(stream, tester);
    }

    static <E> boolean andCombine(Stream<E> stream, Predicate<E> tester) {
        return stream.allMatch(tester);
    }

    static <E> boolean orCombine(Stream<E> stream, Predicate<E> tester) {
        return stream.anyMatch(tester);
    }

    static <E> boolean andNotCombine(Stream<E> stream, Predicate<E> tester) {
        return stream.noneMatch(tester);
    }

    static <E> boolean orNotCombine(Stream<E> stream, Predicate<E> tester) {
        return stream.anyMatch(v -> !tester.test(v));
    }
}
