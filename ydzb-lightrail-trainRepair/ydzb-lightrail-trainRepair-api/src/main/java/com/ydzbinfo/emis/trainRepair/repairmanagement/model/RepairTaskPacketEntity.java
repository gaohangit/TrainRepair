package com.ydzbinfo.emis.trainRepair.repairmanagement.model;

import java.util.List;

/**
 * Description: 检修任务包实体(加字段——新创建的故障任务包或否）
 * Author: wuyuechang
 * Create Date Time: 2021/4/20 8:56
 * Update Date Time: 2021/4/20 8:56
 *
 * @see
 */
public class RepairTaskPacketEntity {

    /**
     * 任务包ID
     */
    private String taskId;

    /**
     * 作业包名称
     */
    private String packetName;

    /**
     * 作业包类型
     */
    private String packetTypeCode;


    /**
     * 作业包编码
     */
    private String packetCode;

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 配件ID
     */
    private String partId;

    /**
     * 所编码
     */
    private String deptCode;

    /**
     * 班次
     */
    private String dayplanId;

    /**
     * 检修班组编码
     */
    private String repairDeptCode;

    /**
     * 检修班组名称
     */
    private String repairDeptName;

    /**
     * 检修股道编码
     */
    private String repairTrackCode;

    /**
     * 检修股道名称
     */
    private String repairTrackName;

    /**
     * 状态
     */
    private String status;

    /**
     * 任务集合
     */
    private List<RepairTaskItemEntity> taskList;

    /**
     * 所属单位编码
     */
    private String depotCode;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPacketName() {
        return packetName;
    }

    public void setPacketName(String packetName) {
        this.packetName = packetName;
    }

    public String getPacketTypeCode() {
        return packetTypeCode;
    }

    public void setPacketTypeCode(String packetTypeCode) {
        this.packetTypeCode = packetTypeCode;
    }

    public String getPacketCode() {
        return packetCode;
    }

    public void setPacketCode(String packetCode) {
        this.packetCode = packetCode;
    }

    public String getTrainsetId() {
        return trainsetId;
    }

    public void setTrainsetId(String trainsetId) {
        this.trainsetId = trainsetId;
    }

    public String getTrainsetName() {
        return trainsetName;
    }

    public void setTrainsetName(String trainsetName) {
        this.trainsetName = trainsetName;
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDayplanId() {
        return dayplanId;
    }

    public void setDayplanId(String dayplanId) {
        this.dayplanId = dayplanId;
    }

    public String getRepairDeptCode() {
        return repairDeptCode;
    }

    public void setRepairDeptCode(String repairDeptCode) {
        this.repairDeptCode = repairDeptCode;
    }

    public String getRepairDeptName() {
        return repairDeptName;
    }

    public void setRepairDeptName(String repairDeptName) {
        this.repairDeptName = repairDeptName;
    }

    public String getRepairTrackCode() {
        return repairTrackCode;
    }

    public void setRepairTrackCode(String repairTrackCode) {
        this.repairTrackCode = repairTrackCode;
    }

    public String getRepairTrackName() {
        return repairTrackName;
    }

    public void setRepairTrackName(String repairTrackName) {
        this.repairTrackName = repairTrackName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RepairTaskItemEntity> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<RepairTaskItemEntity> taskList) {
        this.taskList = taskList;
    }

    public String getDepotCode() {
        return depotCode;
    }

    public void setDepotCode(String depotCode) {
        this.depotCode = depotCode;
    }
}
