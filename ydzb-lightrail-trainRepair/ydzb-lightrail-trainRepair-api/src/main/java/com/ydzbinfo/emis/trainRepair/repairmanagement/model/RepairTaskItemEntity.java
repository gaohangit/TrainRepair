package com.ydzbinfo.emis.trainRepair.repairmanagement.model;

/**
 * Description: 检修任务实体
 * Author: wuyuechang
 * Create Date Time: 2021/4/20 8:56
 * Update Date Time: 2021/4/20 8:56
 *
 * @see
 */
public class RepairTaskItemEntity {

    /**
     * 任务ID
     */
    private String taskItemId;

    /**
     * 任务包ID
     */
    private String taskId;

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 班次
     */
    private String dayplanId;

    /**
     * 配件ID
     */
    private String partId;

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 项目类型
     */
    private String itemType;

    /**
     * 辆序
     */
    private String carNo;

    /**
     * 功能位编码
     */
    private String positionCode;

    /**
     * 功能位名称
     */
    private String positionName;

    /**
     * 位置编码
     */
    private String locationCode;

    /**
     * 位置名称
     */
    private String locationName;

    /**
     * 实体件编码
     */
    private String modelCode;

    /**
     * 实体件名称
     */
    private String modelName;

    /**
     * 配件类型编码
     */
    private String keyPartTypeId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private String status;

    /**
     * 所属单位编码
     */
    private String depotCode;

    public String getTaskItemId() {
        return taskItemId;
    }

    public void setTaskItemId(String taskItemId) {
        this.taskItemId = taskItemId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public String getDayplanId() {
        return dayplanId;
    }

    public void setDayplanId(String dayplanId) {
        this.dayplanId = dayplanId;
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getKeyPartTypeId() {
        return keyPartTypeId;
    }

    public void setKeyPartTypeId(String keyPartTypeId) {
        this.keyPartTypeId = keyPartTypeId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepotCode() {
        return depotCode;
    }

    public void setDepotCode(String depotCode) {
        this.depotCode = depotCode;
    }
}
