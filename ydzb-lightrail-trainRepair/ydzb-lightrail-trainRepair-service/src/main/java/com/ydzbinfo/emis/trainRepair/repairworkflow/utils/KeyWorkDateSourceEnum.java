package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

/**
 * 获取关键作业数据来源字典信息
 */
public enum KeyWorkDateSourceEnum {
    PURE_MANUAL("PURE_MANUAL", "手动录入"),
    FAULT("FAULT","故障数据");

    String value;
    String label;

    KeyWorkDateSourceEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }


    public String getValue() {
        return value;
    }
}
