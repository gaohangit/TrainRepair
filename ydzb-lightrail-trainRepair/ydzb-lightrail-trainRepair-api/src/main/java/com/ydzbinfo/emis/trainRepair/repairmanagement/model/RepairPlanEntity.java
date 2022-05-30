package com.ydzbinfo.emis.trainRepair.repairmanagement.model;

import java.util.List;

/**
 * Description: 检修计划前台实体
 * Author: wuyuechang
 * Create Date Time: 2021/4/20 8:59
 * Update Date Time: 2021/4/20 8:59
 *
 * @see
 */
public class RepairPlanEntity {

    /**
     * 车组号
     */
    private String trainsetName;

    /**
     * 日计划
     */
    private String dayplanId;

    /**
     * 所编码
     */
    private String unitCode;

    /**
     * 车组code
     */
    private String trainsetCode;

    /**
     * 出所车次
     */
    private String outTrainNo;

    /**
     * 出所时间
     */
    private String outTime;

    /**
     * 计划检修股道
     */
    private String planRepairTrack;

    /**
     * 计划检修开始时间
     */
    private String planRepairBeginTime;

    /**
     * 计划入库时间
     */
    private String repairPlanInTime;

    /**
     * 入库股道名称
     */
    private String trackName;

    /**
     * 入库股道code
     */
    private String trackCode;

    /**
     * 作业包集合
     */
    private List<String> repairPackets;

    public String getTrainsetName() {
        return trainsetName;
    }

    public void setTrainsetName(String trainsetName) {
        this.trainsetName = trainsetName;
    }

    public String getTrainsetCode() {
        return trainsetCode;
    }

    public void setTrainsetCode(String trainsetCode) {
        this.trainsetCode = trainsetCode;
    }

    public String getRepairPlanInTime() {
        return repairPlanInTime;
    }

    public void setRepairPlanInTime(String repairPlanInTime) {
        this.repairPlanInTime = repairPlanInTime;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackCode() {
        return trackCode;
    }

    public void setTrackCode(String trackCode) {
        this.trackCode = trackCode;
    }

    public String getPlanRepairTrack() {
        return planRepairTrack;
    }

    public void setPlanRepairTrack(String planRepairTrack) {
        this.planRepairTrack = planRepairTrack;
    }

    public List<String> getRepairPackets() {
        return repairPackets;
    }

    public void setRepairPackets(List<String> repairPackets) {
        this.repairPackets = repairPackets;
    }

    public String getDayplanId() {
        return dayplanId;
    }

    public void setDayplanId(String dayplanId) {
        this.dayplanId = dayplanId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getOutTrainNo() {
        return outTrainNo;
    }

    public void setOutTrainNo(String outTrainNo) {
        this.outTrainNo = outTrainNo;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getPlanRepairBeginTime() {
        return planRepairBeginTime;
    }

    public void setPlanRepairBeginTime(String planRepairBeginTime) {
        this.planRepairBeginTime = planRepairBeginTime;
    }
}
