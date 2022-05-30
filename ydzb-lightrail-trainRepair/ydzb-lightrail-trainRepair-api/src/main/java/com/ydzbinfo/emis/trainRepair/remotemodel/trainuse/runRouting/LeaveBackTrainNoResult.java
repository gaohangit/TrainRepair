package com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.runRouting;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:     出入所车次查询参数实体
 * Author: 韩旭
 * Create Date Time: 2021/8/23 10:37
 *
 * @see
 */
@Data
public class LeaveBackTrainNoResult implements Serializable {
    private String backTrainNo;
    private String depTrainNo;
}
