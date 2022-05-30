package com.ydzbinfo.emis.trainRepair.taskAllot.model;


import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotpacket;

public class PacketData {

    /**
     * 作业类型CODE
     */
    private String workTypeCode;

    /**
     * 作业类型名称
     */
    private String workTypeName;

    /**
     * 作业任务CODE
     */
    private String workTaskCode;

    /**
     * 作业任务名称
     */
    private String workTaskName;

    /**
     * 作业任务显示名称
     */
    private String workTaskDisplayName;

    /**
     * 派工状态Code
     */
    private String allotStateCode;

    /**
     * 派工状态名称
     */
    private String allotStateName;

    /**
     * 派工人员显示名称
     */
    private String workerDisplayName;

    /**
     * 派工模式类型
     */
    private String workModeTypeCode;

    /**
     * 派工模式类型名称
     */
    private String workModeTypeName;

    /**
     * 显示模式
     */
    private String showMode;

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 派工包
     */
    private XzyMTaskallotpacket packet;

    public String getWorkTypeCode() {
        return workTypeCode;
    }

    public void setWorkTypeCode(String workTypeCode) {
        this.workTypeCode = workTypeCode;
    }

    public String getWorkTypeName() {
        return workTypeName;
    }

    public void setWorkTypeName(String workTypeName) {
        this.workTypeName = workTypeName;
    }

    public String getWorkTaskCode() {
        return workTaskCode;
    }

    public void setWorkTaskCode(String workTaskCode) {
        this.workTaskCode = workTaskCode;
    }

    public String getWorkTaskName() {
        return workTaskName;
    }

    public void setWorkTaskName(String workTaskName) {
        this.workTaskName = workTaskName;
    }

    public String getWorkTaskDisplayName() {
        return workTaskDisplayName;
    }

    public void setWorkTaskDisplayName(String workTaskDisplayName) {
        this.workTaskDisplayName = workTaskDisplayName;
    }

    public String getAllotStateCode() {
        return allotStateCode;
    }

    public void setAllotStateCode(String allotStateCode) {
        this.allotStateCode = allotStateCode;
    }

    public String getAllotStateName() {
        return allotStateName;
    }

    public void setAllotStateName(String allotStateName) {
        this.allotStateName = allotStateName;
    }

    public String getWorkerDisplayName() {
        return workerDisplayName;
    }

    public void setWorkerDisplayName(String workerDisplayName) {
        this.workerDisplayName = workerDisplayName;
    }

    public String getWorkModeTypeCode() {
        return workModeTypeCode;
    }

    public void setWorkModeTypeCode(String workModeTypeCode) {
        this.workModeTypeCode = workModeTypeCode;
    }

    public String getWorkModeTypeName() {
        return workModeTypeName;
    }

    public void setWorkModeTypeName(String workModeTypeName) {
        this.workModeTypeName = workModeTypeName;
    }

    public String getShowMode() {
        return showMode;
    }

    public void setShowMode(String showMode) {
        this.showMode = showMode;
    }

    public XzyMTaskallotpacket getPacket() {
        return packet;
    }

    public void setPacket(XzyMTaskallotpacket packet) {
        this.packet = packet;
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
}
