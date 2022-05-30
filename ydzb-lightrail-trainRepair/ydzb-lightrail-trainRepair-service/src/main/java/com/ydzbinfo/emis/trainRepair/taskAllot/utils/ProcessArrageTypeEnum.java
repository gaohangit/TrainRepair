package com.ydzbinfo.emis.trainRepair.taskAllot.utils;

import com.baomidou.mybatisplus.enums.IEnum;

/**
 * 作业作业类型
 *
 * @author 张天可
 */
public enum ProcessArrageTypeEnum implements IEnum {
    CAR("0", "辆序"),
    PART("1", "部件");

    String value;
    String label;

    ProcessArrageTypeEnum(String value, String label) {
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
