package com.ydzbinfo.emis.trainRepair.constant;

import com.baomidou.mybatisplus.enums.IEnum;
import com.ydzbinfo.emis.utils.EnumUtils;

/**
 * 编组形式
 */
public enum MarshallingTypeEnum implements IEnum {
    FIRST(1, "前"),
    SECOND(2, "后"),
    ALL(3, "全部");

    Integer value;
    String label;

    MarshallingTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getValue() {
        return value;
    }

    public static String getLabelByKey(Integer key) {
        return EnumUtils.findValue(MarshallingTypeEnum.class, MarshallingTypeEnum::getValue, MarshallingTypeEnum::getLabel, key);
    }
}
