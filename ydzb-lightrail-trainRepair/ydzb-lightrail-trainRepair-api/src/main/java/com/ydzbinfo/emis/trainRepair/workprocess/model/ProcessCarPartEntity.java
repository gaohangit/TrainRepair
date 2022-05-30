package com.ydzbinfo.emis.trainRepair.workprocess.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/7/2 09:46
 * @Description: 作业过程项目实体（到辆序）—中台
 */
@Data
public class ProcessCarPartEntity implements Serializable {

    /**
     * 主键
     */
    private String processId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 部件类型
     */
    private String partType;

    /**
     * 部件名称
     */
    private String partName;

    /**
     * 部件位置
     */
    private String partPosition;

    /**
     * 计划项目ID
     */
    // private String taskItemId;

    /**
     * 检修方式
     */
    private String repairMode;

    /**
     * 辆序
     */
    private String carNo;

    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 项目类型
     */
    private String itemType;

    /**
     * 项目唯一编码
     */
    private String publishCode;

    /**
     * 项目作业类型
     */
    private String arrageType;

    /**
     *作业项目编码
     */
    private String itemCode;

    /**
     *作业项目名称
     */
    private String itemName;

    /**
     * 作业包主键
     */
    private String processPacketId;

    /**
     * 作业部门主键
     */
    private String processDeptId;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 作业人员实体集合
     */
    List<ProcessPersonEntity> processPersonEntityList;
}
