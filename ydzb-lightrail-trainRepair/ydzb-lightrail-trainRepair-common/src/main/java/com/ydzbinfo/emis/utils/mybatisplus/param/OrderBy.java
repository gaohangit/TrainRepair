package com.ydzbinfo.emis.utils.mybatisplus.param;

import lombok.Getter;


/**
 * 用于封装sql排序信息，此对象对应单个字段的排序，如果需要多字段排序请按顺序传递多个
 *
 * @author 张天可
 */
@Getter
public class OrderBy<T> {
    private final String column;
    private final boolean isAsc;
    private final Class<T> tClass;

    OrderBy(String column, boolean isAsc, Class<T> tClass) {
        this.column = column;
        this.isAsc = isAsc;
        this.tClass = tClass;
    }
}
