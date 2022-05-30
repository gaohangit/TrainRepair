package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import lombok.Data;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/22 12:08
 **/
@Data
public class RepairItemVo {
    //检修项目主键ID
    private String itemId;
    //维修卡片主键
    private String cardId;
    //检修项目编码
    private String itemCode;
    //项目名称
    private String itemName;
    //车型
    private String trainsetType;

}
