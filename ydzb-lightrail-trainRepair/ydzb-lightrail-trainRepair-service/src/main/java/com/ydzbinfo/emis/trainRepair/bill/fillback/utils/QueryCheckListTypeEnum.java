package com.ydzbinfo.emis.trainRepair.bill.fillback.utils;

import lombok.Getter;

/**
 * @Description: 查询记录单时的查询属性
 * @Data: 2022/01/20
 * @Author: 史艳涛
 */
@Getter
public enum QueryCheckListTypeEnum {

    ONE_TWO_REPAIR("OneTwoRepair", "一二级修"),

    LIVE_CHECK("LiveCheck","出所联检"),

    INTEGRATION("Integration","一体化作业申请"),

    CUSTOM("Custom","自定义"),

    RE_CHECK("ReCheck","复核"),

    EQUIPMENT("Equipment","机检一级修");

    private final String value;
    private final String text;

    QueryCheckListTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
