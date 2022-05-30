package com.ydzbinfo.emis.trainRepair.bill.fillback.utils;

import lombok.Getter;

/**
 * @Description:
 * @Data: 2021/8/9
 * @Author: 冯帅
 */
@Getter
public enum SummaryTemplateTypeEnum {
    CYC_ONE("RECORD_FIRST", MainCycEnum.MAIN_CYC_1),
    CYC_TWO("RECORD_SECOND", MainCycEnum.MAIN_CYC_2),
    CYC_TEMP("RECORD_TEMP", MainCycEnum.MAIN_CYC_TEMP);

    private final String value;
    private final MainCycEnum mainCycEnum;

    SummaryTemplateTypeEnum(String value, MainCycEnum mainCycEnum) {
        this.value = value;
        this.mainCycEnum = mainCycEnum;
    }
}
