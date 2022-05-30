package com.ydzbinfo.emis.utils.mybatisplus.param;

/**
 * @author 张天可
 * @since 2022/4/22
 */
public interface ColumnParam<T> extends LogicalLinkable<T> {
    String getColumnName();

    ParamOperator getOperator();

    Object getParamValue();
}
