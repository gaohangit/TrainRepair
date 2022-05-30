package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import lombok.Data;

/**
 * @description: 记录单-检修工长或质检员批量签字时，部分签字成功时返回的实体类信息
 * @date: 2022/03/28
 * @author: 史艳涛
 */
@Data
public class RestMessage {

    /**
     * 错误提示信息
     */
    private String warnMsg;
}
