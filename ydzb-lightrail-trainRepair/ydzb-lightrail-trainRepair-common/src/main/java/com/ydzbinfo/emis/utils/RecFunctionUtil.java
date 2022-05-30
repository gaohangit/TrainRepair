package com.ydzbinfo.emis.utils;

import java.util.function.Function;

/**
 * lambda表达式递归工具类
 *
 * @author 张天可
 * @since 2021/10/21
 */
public class RecFunctionUtil {

    @FunctionalInterface
    public interface RecFunction<T, R> {
        R apply(T t, RecFunction<T, R> self);
    }

    public static <T, R> Function<T, R> makeRec(RecFunction<T, R> recFunction) {
        return t -> recFunction.apply(t, recFunction);
    }

    // @FunctionalInterface
    // public interface RecFunction2<T, R> {
    //     Function<T, R> apply(RecFunction2<T, R> self);
    //
    //     default R getAndApply(T t) {
    //         return this.apply(this).apply(t);
    //     }
    // }
    //
    // @Data
    // public static class ValueWrapper<T> {
    //     T value;
    // }
    //
    // public static void main(String[] args) {
    //     RecFunction2<Integer, Integer> recFunction = (recFunction2) -> (v1) -> {
    //         if (v1 > 1) {
    //             System.out.println(v1);
    //             return recFunction2.getAndApply(v1 - 1);
    //         } else {
    //             System.out.println("asd");
    //             return v1;
    //         }
    //     };
    //     System.out.println(recFunction.getAndApply(10));
    // }
}
