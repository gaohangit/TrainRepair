package com.ydzbinfo.emis.trainRepair.remotemodel.item;

import lombok.Data;

/**
 * @description: 项目维修周期
 * @date: 2021/11/26
 * @author: 史艳涛
 */
@Data
public class ItemCycleInfo {

    /**
     * 项目周期主键ID
     */
    private String itemCycId;

    /**
     * 周期类别ID
     */
    private String cycTypeId;

    /**
     * 周期维度ID
     */
    private String cycCateId;

    /**
     * 检修项目主键
     */
    private String itemId;

    /**
     * 生效周期类型
     */
    private String validCycType;

    /**
     * 生效周期子类型
     */
    private String validSubCycType;

    /**
     * 生效周期
     */
    private String validCyc;

    /**
     * 周期值
     */
    private int cycValue;

    /**
     * 周期上限
     */
    private int cycMinusValue;

    /**
     * 周期下限
     */
    private int cycPlusValue;

    /**
     * 生效时间（当前）
     */
    private String startDate;

    /**
     * 失效时间（最大）
     */
    private String endDate;

    /**
     * 启用标志
     */
    private String usedFlag;
}
