package com.ydzbinfo.emis.trainRepair.bill.fillback.utils;

import com.ydzbinfo.emis.trainRepair.bill.model.bill.MainCyc;
import lombok.Getter;

/**
 * @Description:
 * @Data: 2021/8/9
 * @Author: 冯帅
 */
@Getter
public enum MainCycEnum {
    MAIN_CYC_1(new MainCyc("1","一级修")),
    MAIN_CYC_2(new MainCyc("2","二级修")),
    MAIN_CYC_TEMP(new MainCyc("-1","临时任务"));


    MainCyc mainCyc;

    MainCycEnum(MainCyc mainCyc) {
        this.mainCyc = mainCyc;
    }
}
