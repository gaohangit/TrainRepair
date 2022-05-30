package com.ydzbinfo.emis.trainRepair.constant;

import com.baomidou.mybatisplus.enums.IEnum;

/**
 * 作业过程记录状态
 *
 * @author 张天可
 */
public enum ProcessRecordStateEnum implements IEnum {
    BEGIN("0", "开始"),
    SUSPEND("1", "暂停"),
    RESUME("2", "继续"),
    END("3", "结束");

    String value;
    String label;

    ProcessRecordStateEnum(String value, String label) {
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
