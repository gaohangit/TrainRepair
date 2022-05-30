package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

/**
 * 流程状态排序等级
 */
public enum FlowRunStateLevel {
    UNDER_WAY("0", 1),
    NOT_STARTED("-1", 2),
    HAVE_FINISHED("1",3);
    String value;
    int label;


    FlowRunStateLevel(String value, int label) {
        this.value = value;
        this.label = label;

    }

    public int getLabel() {
        return label;
    }


    public String getValue() {
        return value;
    }
}
