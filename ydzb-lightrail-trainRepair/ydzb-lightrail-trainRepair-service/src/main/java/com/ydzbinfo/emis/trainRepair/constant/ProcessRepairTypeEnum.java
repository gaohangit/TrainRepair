package com.ydzbinfo.emis.trainRepair.constant;

import com.baomidou.mybatisplus.enums.IEnum;

/**
 * 作业过程检修类型
 *
 * @author 张天可
 */
public enum ProcessRepairTypeEnum implements IEnum {
    ONE("0", "一级修"),
    TWO("1", "二级修"),
    UN_REPAIR("6", "无修程");//6为协同任务 3为临时任务，目前只有协同任务

    String value;
    String label;

    ProcessRepairTypeEnum(String value, String label) {
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
