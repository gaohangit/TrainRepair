package com.ydzbinfo.emis.trainRepair.bill.fillback.utils;

import com.ydzbinfo.emis.trainRepair.bill.model.bill.FillState;
import lombok.Getter;

/**
 * @Description:
 * @Data: 2021/8/9
 * @Author: 韩旭
 */
@Getter
public enum FillStateEnum {
    FillState_CYC_0(new FillState("0","未完全回填")),
    FillState_CYC_1(new FillState("1","完全回填")),
    FillState_CYC_2(new FillState("2","工长签字")),
    FillState_CYC_3(new FillState("3","质检签字")),
    ;

    FillState fillState;
    FillStateEnum(FillState fillState) {
        this.fillState = fillState;
    }
}
