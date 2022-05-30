package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base;

/**
 * @author 张天可
 * @since 2021/7/6
 */
public interface IExtraInfoBase {
    String getType();

    String getValue();

    void setType(String type);

    void setValue(String value);
}
