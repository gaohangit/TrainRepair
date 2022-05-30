package com.ydzbinfo.emis.trainRepair.constant;

import lombok.Getter;

/**
 * @Description: 修程
 * @Data: 2022/01/28
 * @Author: 史艳涛
 */
@Getter
public enum MainCycEnum {

    MAIN_CYC_1("1", "一级修"),

    MAIN_CYC_2("2", "二级修"),

    MAIN_CYC_TEMP("-1", "临时任务");

    private final String value;
    private final String text;

    MainCycEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
