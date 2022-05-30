package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;

import java.util.List;

/**
 * @author 高晗
 * @description
 * @createDate 2021/10/25 15:14
 **/
@Data
public class FlowRunInfoWithStatistics {
    /**
     * 刷卡记录数据
     */
    private List<FlowRunInfoWithKeyWorkBase> flowRunInfoWithKeyWorkBases;

    /**
     * 统计数据条数
     */
    private String statisticsResult;

    /**
     * 自然列/标准组统计
     */
    private String standardGroupResult;

}
