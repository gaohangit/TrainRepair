package com.ydzbinfo.emis.trainRepair.constant;

import com.baomidou.mybatisplus.enums.IEnum;

/**
 * 作业过程人员类型
 *
 * @author 张天可
 */
public enum ProcessWorkerTypeEnum implements IEnum {
    REPAIR("1", "检修人员"),
    QUALITY("2", "质检人员"),
    UN_REPAIR_WORK("3", "作业人员"),
    UN_REPAIR_CONFIRM("4", "确认人员");

    String value;
    String label;

    ProcessWorkerTypeEnum(String value, String label) {
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
