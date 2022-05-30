package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import lombok.Data;

/**
 * 检修工长和质检员签字信息
*/
@Data
public class SignMessage {

    /**
     * 结果编码(1：完全成功；0：部分成功；-1：全部失败)
     */
    private String code;
    /**
     * 返回信息
     */
    private String msg;
}
