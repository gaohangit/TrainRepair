package com.ydzbinfo.emis.trainRepair.remotemodel.item;

import lombok.Data;

/**
 * @description: 项目所属部件
 * @date: 2021/11/26
 * @author: 史艳涛
 */
@Data
public class ItemPartsInfo {

    /**
     * 适用部件主键
     */
    private String itemApplyPartsId;

    /**
     * 检修项目主键
     */
    private String itemId;

    /**
     * 配件类型Id
     */
    private String partsTypeId;

    /**
     * 配件名称
     */
    private String partsTypeName;

    /**
     * 配件ID
     */
    private String partsId;

    /**
     * 配件名称
     */
    private String partsName;
}
