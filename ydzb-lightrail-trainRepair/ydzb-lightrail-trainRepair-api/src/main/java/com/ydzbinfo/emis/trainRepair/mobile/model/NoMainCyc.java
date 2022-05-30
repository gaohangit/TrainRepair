package com.ydzbinfo.emis.trainRepair.mobile.model;

import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrackPowerEntity;
import lombok.Data;

import java.util.List;

/**
 * Description:     一体化返回页面信息
 * Author: wuyuechang
 * Create Date Time: 2021/5/13 15:35
 * Update Date Time: 2021/5/13 15:35
 *
 * @see
 */
@Data
public class NoMainCyc {

    /**
     * 车组id
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 日计划id
     */
    private String dayPlanId;

    /**
     * 部门Code
     */
    private String deptCode;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 运用所Code
     */
    private String unitCode;

    /**
     * 运用所名称
     */
    private String unitName;

    /**
     * 作业人员id
     */
    private String workerId;

    /**
     * 作业人员名称
     */
    private String workerName;

    /**
     * 作业人员类型（1--检修，2--质检，3-无修程任务完成，4-无修程任务确认）
     */
    private String workerType;

    /**
     * 股道位列
     */
    private String planRepairTrack;

    /**
     * 车辆编组数
     */
    private String iMarshalcount;

    /**
     * 列位供断电信息
     */
    private List<TrackPowerEntity> trackPowerEntityList;

    /**
     * 无修程包返回信息
     */
    private List<NoMainCycPacketInfo> noMainCycPacketInfos;
}
