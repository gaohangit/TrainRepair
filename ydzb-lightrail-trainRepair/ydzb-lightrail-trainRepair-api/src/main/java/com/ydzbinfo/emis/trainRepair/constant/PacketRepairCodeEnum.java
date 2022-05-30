package com.ydzbinfo.emis.trainRepair.constant;

/**
 * 包检修编码（修程编码）
 */
public enum PacketRepairCodeEnum {

    REPAIR_ONE("1", "一级修"),
    REPAIR_TWO("2", "二级修"),
    NONE("-1", "无");

    String value;
    String label;

    PacketRepairCodeEnum(String value, String label) {
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
