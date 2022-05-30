package com.ydzbinfo.emis.trainRepair.remotemodel.item;

import lombok.Data;

import java.util.List;

/**
* @description: 项目详细信息
* @date: 2021/11/26
* @author: 史艳涛
*/
@Data
public class RepairItemInfo {

    /**
     * 检修项目主键ID
     */
    private String itemId;

    /**
     * 检修项目编码
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 工艺卡片编码
     */
    private String mainTainCardNo;

    /**
     * 所属功能分类编码
     */
    private String funcSysCode;

    /**
     * 所属功能分类名称
     */
    private String funcSysName;

    /**
     * 维修性质(Y预防性;G更正性)
     */
    private String mainTainNature;

    /**
     * 项目等级
     */
    private String	itemLevel;
    /**
     * 项目检修周期集合
     */
    private List<ItemCycleInfo> itemCycleVos;

    /**
     * 适用部件集合
     */
    private List<ItemPartsInfo> itemPartVos;

    /**
     * 适用维修周期信息字符串
     */
    private String strItemCycle;

    /**
     * 部件字符串
     */
    private String strPartsTypeName;

    /**
     * 维修方式字符串
     */
    private String strRepairMethod;
}
