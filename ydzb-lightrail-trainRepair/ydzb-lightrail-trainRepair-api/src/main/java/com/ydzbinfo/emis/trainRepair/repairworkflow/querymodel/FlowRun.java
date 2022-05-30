package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.utils.entity.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 流程运行表
 * </p>
 *
 * @author gaohan
 * @since 2021-06-06
 */
@TableName("XZY_M_FLOWRUN")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowRun implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 流程主键
     */
    @TableField("S_FLOWID")
    private String flowId;

    /**
     * 流程类型标识
     */
    @TableField("S_FLOWTYPECODE")
    private String flowTypeCode;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayPlanId;

    /**
     * 车组id
     */
    @TableField("S_TRAINSETID")
    private String trainsetId;

    /**
     * 股道code
     */
    @TableField("S_TRACKCODE")
    private String trackCode;

    /**
     * 车头所在列位code
     */
    @TableField("S_HEADTRACKPOSITIONCODE")
    private String headTrackPositionCode;

    /**
     * 车尾所在列位code
     */
    @TableField("S_TAILTRACKPOSITIONCODE")
    private String tailTrackPositionCode;

    /**
     * 股道名称
     */
    @TableField("S_TRACKNAME")
    private String trackName;

    /**
     * 车头所在列位名称
     */
    @TableField("S_HEADTRACKPOSITIONNAME")
    private String headTrackPositionName;

    /**
     * 车尾所在列位名称
     */
    @TableField("S_TAILTRACKPOSITIONNAME")
    private String tailTrackPositionName;

    /**
     * 启动方式(AUTO 系统自动开始, PERSON 人员操作开始)
     */
    @TableField("S_STARTTYPE")
    private String startType;

    /**
     * 启动时间
     */
    @TableField("D_STARTTIME")
    @JSONField(format = Constants.DEFAULT_DATE_TIME_FORMAT)
    private Date startTime;

    /**
     * 启动者id
     */
    @TableField("S_STARTWORKERID")
    private String startWorkerId;

    /**
     * 启动者名称
     */
    @TableField("S_STARTWORKERNAME")
    private String startWorkerName;

    /**
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 流程运行状态 0:启动 1:结束
     */
    @TableField("S_STATE")
    private String state;
}
