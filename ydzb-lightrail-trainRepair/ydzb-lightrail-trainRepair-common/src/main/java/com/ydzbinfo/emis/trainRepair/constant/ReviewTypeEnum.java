package com.ydzbinfo.emis.trainRepair.constant;

import lombok.Getter;

/**
 * @Description: 复核任务类型
 * @Data: 2022/01/18
 * @Author: 史艳涛
 */
@Getter
public enum ReviewTypeEnum {

    REVIEW_WHEEL("0","轮对复核"),

    REVIEW_PANTOGRAPH("1", "受电弓复核");

    private final String value;
    private final String text;

    ReviewTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
