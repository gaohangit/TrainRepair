package com.ydzbinfo.emis.trainRepair.statistics.model;

import lombok.Data;

import java.util.List;

/**
 * Description:     作业时长统计入参
 * Author: 吴跃常
 * Create Date Time: 2021/6/22 11:14
 * Update Date Time: 2021/6/22 11:14
 *
 * @see
 */
@Data
public class DurationEntity {

    /**
     * 开始时间
     */
    private long startTime;

    /**
     * 结束时间
     */
    private long endTime;

    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 车组名称
     */
    private List<String> trainsetNames;

    /**
     * 班组名称
     */
    private List<String> deptNames;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 开始日计划
     */
    private String startDate;

    /**
     * 结束日计划
     */
    private String endDate;

    /**
     * 所编码
     */
    private String unitCode;

    /**
     * 页数
     */
    private int page = 1;

    /**
     * 条数
     */
    private int limit = 10;
}
