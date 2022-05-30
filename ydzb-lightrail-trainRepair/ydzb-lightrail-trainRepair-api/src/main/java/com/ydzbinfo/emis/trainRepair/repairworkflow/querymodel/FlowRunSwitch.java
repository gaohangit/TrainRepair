package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 流程运行切换记录表
 * </p>
 *
 * @author 高晗
 * @since 2022-01-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_M_FLOWRUNSWITCH")
public class FlowRunSwitch implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 原流程运行表主键
     */
    @TableField("S_SOURCEFLOWRUNID")
    private String sourceFlowRunId;

    /**
     * 原流程表主键
     */
    @TableField("S_SOURCEFLOWID")
    private String sourceFlowId;

    /**
     * 原流程类型标识
     */
    @TableField("S_SOURCEFLOWTYPECODE")
    private String sourceFlowTypeCode;

    /**
     * 目标流程运行表主键
     */
    @TableField("S_TARGETFLOWRUNID")
    private String targetFlowRunId;

    /**
     * 目标流程表主键
     */
    @TableField("S_TARGETFLOWID")
    private String targetFlowId;

    /**
     * 目标流程类型标识
     */
    @TableField("S_TARGETFLOWTYPECODE")
    private String targetFlowTypeCode;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayPlanId;

    /**
     * 切换类型(FORCE: 强制 NORMAL:普通)
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 切换时间
     */
    @TableField("D_SWITCHTIME")
    private Date switchTime;

    /**
     * 切换者id
     */
    @TableField("S_WORKERID")
    private String workerId;

    /**
     * 切换者名称
     */
    @TableField("S_WORKERNAME")
    private String workerName;

    @TableField("S_TRAINSETID")
    private String trainsetId;


}
