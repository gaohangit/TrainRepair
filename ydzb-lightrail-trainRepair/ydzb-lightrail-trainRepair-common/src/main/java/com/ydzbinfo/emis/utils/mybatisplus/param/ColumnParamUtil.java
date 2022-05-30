package com.ydzbinfo.emis.utils.mybatisplus.param;

import org.springframework.util.ObjectUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 过滤空条件
 * @author 张天可
 * @since 2022/4/22
 */
public class ColumnParamUtil {
    public static <T> boolean blankFilter(ColumnParam<T> columnParam) {
        return columnParam != null && !ObjectUtils.isEmpty(columnParam.getParamValue());
    }

    @SuppressWarnings("unchecked")
    static <T> ColumnParam<T>[] columnParamArrayGenerator(int value) {
        return (ColumnParam<T>[]) Array.newInstance(ColumnParam.class, value);
    }

    @SafeVarargs
    public static <T> ColumnParam<T>[] filterBlankParams(ColumnParam<T>... columnParams) {
        return Arrays.stream(columnParams).filter(ColumnParamUtil::blankFilter).toArray(ColumnParamUtil::columnParamArrayGenerator);
    }

    @SafeVarargs
    public static <T> ColumnParam<T>[] asArray(ColumnParam<T>... columnParams) {
        return Arrays.stream(columnParams).toArray(ColumnParamUtil::columnParamArrayGenerator);
    }

    @SafeVarargs
    public static <T> List<ColumnParam<T>> filterBlankParamList(ColumnParam<T>... columnParams) {
        return Arrays.stream(columnParams).filter(ColumnParamUtil::blankFilter).collect(Collectors.toList());
    }
}
