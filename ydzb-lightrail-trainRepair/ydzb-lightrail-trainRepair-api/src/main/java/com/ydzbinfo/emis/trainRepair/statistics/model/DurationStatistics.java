package com.ydzbinfo.emis.trainRepair.statistics.model;

import com.ydzbinfo.emis.trainRepair.statistics.querymodel.DurationInfo;
import lombok.Data;

import java.util.List;

/**
 * Description: 作业时长统计返回实体
 *
 * Author: 吴跃常
 * Create Date Time: 2021/6/23 15:37
 * Update Date Time: 2021/6/23 15:37
 *
 * @see
 */
@Data
public class DurationStatistics {

    /**
     * 车组数量
     */
    private int trainsetCount;

    /**
     * 列数
     */
    private int column;

    /**
     * 总时长数
     */
    private int totalDuration;

    /**
     * 平均时长数
     */
    private long averageTotalDuration;

    /**
     * 有电时长数
     */
    private int yesDuration;

    /**
     * 平均有电时长数
     */
    private long averageYesDuration;

    /**
     * 无电时长数
     */
    private int noDuration;

    /**
     * 平均无电时长数
     */
    private long averageNoDuration;

    /**
     * 有电车组时长信息
     */
    private TrainsetDuration yesTrainsetDuration;

    /**
     * 无电车组时长信息
     */
    private TrainsetDuration noTrainsetDuration;

    /**
     * 日计划时长集合
     */
    private List<DayPlanDuration> dayPlanDurations;

    /**
     * 项目时长集合
     */
    private List<ItemDuration> itemDurations;

    /**
     * 班次时长集合
     */
    private List<DurationInfo> durations;
}
