package com.ydzbinfo.emis.trainRepair.statistics.model;

import lombok.Data;

/**
 * Description:     日计划时长信息
 * Author: 吴跃常
 * Create Date Time: 2021/6/23 10:37
 * Update Date Time: 2021/6/23 10:37
 *
 * @see
 */
@Data
public class DayPlanDuration {

    /**
     * 日计划id
     */
    private String dayPlanId;

    /**
     * 有电时长
     */
    private int yesDuration;

    /**
     * 无电时长
     */
    private int noDuration;
}
