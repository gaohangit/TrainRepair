package com.ydzbinfo.emis.trainRepair.taskAllot.model;


import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotpacket;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotperson;

import java.util.List;

public class AllotData {

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 车型CODE
     */
    private String trainsetTypeCode;

    /**
     * 车型名称
     */
    private String trainsetTypeName;

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
     * 派工包
     */
    private XzyMTaskallotpacket packet;

    /**
     * 派工人员集合
     */
    private List<XzyMTaskallotperson> workerList;

    /**
     * 项目集合
     */
//    private List<XzyMTaskcarpart> itemList;

    /**
     * 排序
     */
    private String sort;

    /**
     * 显示模式
     */
    private String showMode;

    private String taskRepairCode;

    private String fatherId;

    /**
     * 编组数量
     */
    private String marshal;

    public String getMarshal() {
        return marshal;
    }

    public void setMarshal(String marshal) {
        this.marshal = marshal;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getTaskRepairCode() {
        return taskRepairCode;
    }

    public void setTaskRepairCode(String taskRepairCode) {
        this.taskRepairCode = taskRepairCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTrainsetTypeCode() {
        return trainsetTypeCode;
    }

    public void setTrainsetTypeCode(String trainsetTypeCode) {
        this.trainsetTypeCode = trainsetTypeCode;
    }

    public String getTrainsetTypeName() {
        return trainsetTypeName;
    }

    public void setTrainsetTypeName(String trainsetTypeName) {
        this.trainsetTypeName = trainsetTypeName;
    }

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

    public XzyMTaskallotpacket getPacket() {
        return packet;
    }

    public void setPacket(XzyMTaskallotpacket packet) {
        this.packet = packet;
    }

    public List<XzyMTaskallotperson> getWorkerList() {
        return workerList;
    }

    public void setWorkerList(List<XzyMTaskallotperson> workerList) {
        this.workerList = workerList;
    }
//
//    public List<XzyMTaskcarpart> getItemList() {
//        return itemList;
//    }
//
//    public void setItemList(List<XzyMTaskcarpart> itemList) {
//        this.itemList = itemList;
//    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getShowMode() {
        return showMode;
    }

    public void setShowMode(String showMode) {
        this.showMode = showMode;
    }

    @Override
    public String toString() {
        return "AllotData{" +
                "id='" + id + '\'' +
                ", trainsetId='" + trainsetId + '\'' +
                ", trainsetName='" + trainsetName + '\'' +
                ", trainsetTypeCode='" + trainsetTypeCode + '\'' +
                ", trainsetTypeName='" + trainsetTypeName + '\'' +
                ", workTypeCode='" + workTypeCode + '\'' +
                ", workTypeName='" + workTypeName + '\'' +
                ", workTaskCode='" + workTaskCode + '\'' +
                ", workTaskName='" + workTaskName + '\'' +
                ", workTaskDisplayName='" + workTaskDisplayName + '\'' +
                ", allotStateCode='" + allotStateCode + '\'' +
                ", allotStateName='" + allotStateName + '\'' +
                ", workerDisplayName='" + workerDisplayName + '\'' +
                ", workModeTypeCode='" + workModeTypeCode + '\'' +
                ", workModeTypeName='" + workModeTypeName + '\'' +
                ", packet=" + packet +
                ", workerList=" + workerList +
//                ", itemList=" + itemList +
                ", sort='" + sort + '\'' +
                ", showMode='" + showMode + '\'' +
                '}';
    }
}
