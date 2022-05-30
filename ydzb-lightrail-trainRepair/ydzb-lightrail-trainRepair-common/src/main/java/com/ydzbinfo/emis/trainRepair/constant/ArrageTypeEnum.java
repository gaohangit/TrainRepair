package com.ydzbinfo.emis.trainRepair.constant;

import lombok.Getter;

/**
 * @Description: 作业包类型
 * @Data: 2022/01/17
 * @Author: 史艳涛
 */
@Getter
public enum ArrageTypeEnum {

    ARRAGE_CARNO("0", "辆序"),

    ARRAGE_PART("1", "部件");

    private final String value;
    private final String text;

    ArrageTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
