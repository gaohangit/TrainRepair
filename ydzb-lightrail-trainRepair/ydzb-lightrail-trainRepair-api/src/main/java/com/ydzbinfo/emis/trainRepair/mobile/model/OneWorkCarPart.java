package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.util.Date;

/**
 * Description:         一级修的项目
 * Author: wuyuechang
 * Create Date Time: 2021/5/14 15:35
 * Update Date Time: 2021/5/14 15:35
 *
 * @see
 */
@Data
public class OneWorkCarPart {

    /**
     * 主键
     */
    private String processId;
    /**
     * 日计划ID
     */
    private String dayPlanID;
    /**
     * 车组名称
     */
    private String trainsetName;
    /**
     * 车组ID
     */
    private String trainsetId;
    /**
     * 运用所CODE
     */
    private String unitCode;
    /**
     * 运用所名称
     */
    private String unitName;
    /**
     * 项目CODE
     */
    private String itemCode;
    /**
     * 项目NAME
     */
    private String itemName;
    /**
     * 检修类型（项目类型）
     */
    private String repairType;
    /**
     * 项目作业类型   1--部件  0--辆序
     */
    private String arrageType;
    /**
     * 数据来源
     */
    private String dataSource;
    /**
     * 部件类型名称
     */
    private String partType;
    /**
     * 部件位置
     */
    private String partPosition;
    /**
     * 备注
     */
    private String remark;
    /**
     * 检修方式  0--人工检查   1--机器人     2--机检
     */
    private String repairMode;
    /**
     * 部件名称
     */
    private String partName;
    /**
     * 记录时间
     */
    private Date recordTime;
    /**
     * 记录人姓名
     */
    private String recorderName;
    /**
     * 记录人编码
     */
    private String recorderCode;
    /**
     * 派工包表主键
     */
    private String taskAllotPacketId;
    /**
     * 派工部门表主键
     */
    private String taskAllotDeptId;
    /**
     * 辆序
     */
    private String carNo;
    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 项目时间唯一标识
     */
    private String itemPublished;
}
