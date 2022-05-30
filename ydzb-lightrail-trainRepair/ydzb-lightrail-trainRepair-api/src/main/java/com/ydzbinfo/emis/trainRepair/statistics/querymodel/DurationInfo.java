package com.ydzbinfo.emis.trainRepair.statistics.querymodel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Description:  项目信息对象
 * Author: 吴跃常
 * Create Date Time: 2021/6/22 15:49
 * Update Date Time: 2021/6/22 15:49
 *
 * @see
 */
@Data
@EqualsAndHashCode(exclude = {"trainsetType", "itemName", "itemCode"})
public class DurationInfo {

    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 车组id
     */
    private String trainsetId;

    /**
     * 班组名称
     */
    private String deptName;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 项目code
     */
    private String itemCode;

    /**
     * 日计划id
     */
    private String dayPlanId;

    /**
     * 供断电状态 1有电 2无电
     */
    private String powerState;

    /**
     * 批次
     */
    private String trainsetSubtype;
}
