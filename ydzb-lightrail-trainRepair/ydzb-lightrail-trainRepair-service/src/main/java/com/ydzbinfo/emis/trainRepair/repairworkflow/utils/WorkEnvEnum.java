package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

/**
 * @author 高晗
 * @description 作业条件
 * @createDate 2021/6/22 16:49
 **/
public enum WorkEnvEnum {
    HavePower("1", "有电"),
    NotPower("2", "无电"),
    NoRequirement("","无要求");
    String value;
    String label;


    WorkEnvEnum(String value, String label) {
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
