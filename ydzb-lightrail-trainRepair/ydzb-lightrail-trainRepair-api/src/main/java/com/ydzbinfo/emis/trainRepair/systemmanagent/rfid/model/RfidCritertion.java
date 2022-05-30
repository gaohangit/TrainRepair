package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model;

import lombok.Data;

import java.util.List;

/**
 * Description: 标签标准关系返回数据
 *
 * Author: 吴跃常
 * Create Date Time: 2021/7/28 18:34
 * Update Date Time: 2021/7/28 18:34
 *
 * @see
 */
@Data
public class RfidCritertion {

    /**
     * 主键
     */
    private String id;

    /**
     * 作业位置编码
     */
    private String repairPlaceCode;

    /**
     * 作业位置名称
     */
    private String repairPlaceName;

    /**
     * 项目code
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 阶段
     */
    private String trainsetSubtype;
}
