package com.ydzbinfo.emis.trainRepair.statistics.model;

import lombok.Data;

/**
 * Description:     项目时长对象
 * Author: 吴跃常
 * Create Date Time: 2021/6/23 10:56
 * Update Date Time: 2021/6/23 10:56
 *
 * @see
 */
@Data
public class ItemDuration {

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 项目Code
     */
    private String itemCode;

    /**
     * 项目总时长
     */
    private int itemTotalDuration;

    /**
     * 项目平均时长
     */
    private int itemAverageDuration;

    /**
     * 项目工作人数
     */
    private int itemWorkCount;

    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 批次
     */
    private String trainsetSubtype;
}
