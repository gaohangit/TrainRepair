package com.ydzbinfo.emis.trainRepair.statistics.model;

import lombok.Data;

/**
 * Description: 车组时长信息
 *
 * Author: 吴跃常
 * Create Date Time: 2021/6/23 15:51
 * Update Date Time: 2021/6/23 15:51
 *
 * @see
 */
@Data
public class TrainsetDuration {

    /**
     * 最小车组时长
     */
    private int minTrainsetDuration;

    /**
     * 最小车组名称
     */
    private String minTrainsetName;

    /**
     * 最小班次
     */
    private String minDayPlanId;

    /**
     * 最大车组时长
     */
    private int maxTrainsetDuration;

    /**
     * 最大车组名称
     */
    private String maxTrainsetName;

    /**
     * 最大班次
     */
    private String maxDayPlanId;
}
