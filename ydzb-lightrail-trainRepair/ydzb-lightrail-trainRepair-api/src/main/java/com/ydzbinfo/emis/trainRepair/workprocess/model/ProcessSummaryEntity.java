package com.ydzbinfo.emis.trainRepair.workprocess.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: 冯帅
 * @Date: 2021/7/2 09:49
 * @Description: 作业过程总开始结束时间操作实体—中台
 */
@Data
public class ProcessSummaryEntity implements Serializable {
    /**
     * 主键
     */
    private String recordId;

    /**
     * 日计划ID
     */
    private String dayPlanId;

    /**
     * 车组号ID
     */
    private String trainsetId;

    /**
     * 股道CODE
     */
    private String trackCode;

    /**
     * 股道名称
     */
    private String trackName;

    /**
     * 作业位置名称CODE（列位CODE）
     */
    private String repairPlaceCode;

    /**
     * 作业位置名称NAME（列位名称）
     */
    private String repairPlaceName;

    /**
     * 项目CODE
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 工人ID
     */
    private String stuffId;

    /**
     * 工人名称
     */
    private String stuffName;

    /**
     * 检修时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date repairTime;

    /**
     * 记录时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;

    /**
     * 时间标识  1--开始时间   2--结束时间
     */
    private String timeTag;

    /**
     * 部门CODE
     */
    private String deptCode;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 检修类型（项目类型）
     */
    private String repairType;

    /**
     * 记录人编码
     */
    private String recorderCode;

    /**
     * 记录人名称
     */
    private String recorderName;

    /**
     * 作业标准主键，表XZY_C_WORKCRITERTION
     */
    private String critertionId;

    /**
     * 运用所CODE
     */
    private String unitCode;

    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 数据来源(1,手持，2，pc)
     */
    private String dataSource;
}
