package com.ydzbinfo.emis.trainRepair.bill.fillback.utils;

import lombok.Getter;

/**
 * @Description: 辆序类型（动车、拖车）
 * @Data: 2021/10/26
 * @Author: 史艳涛
 */
@Getter
public enum AxleTypeEnum {

    AXLE_TYPE_MOTOR("M","动"),
    AXLE_TYPE_TRAILER("T","拖");

    private final String value;
    private final String text;

    AxleTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
