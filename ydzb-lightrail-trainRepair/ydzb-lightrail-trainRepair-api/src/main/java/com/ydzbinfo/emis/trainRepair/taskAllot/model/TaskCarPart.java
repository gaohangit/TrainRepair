package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author 冯帅
 * @since 2021-06-05
 */
@TableName("XZY_M_TASKCARPART")
public class TaskCarPart implements Serializable {


    /**
     * 主键
     */
    @TableId("S_PROCESSID")
    private String processId;

    /**
     * 派工包表主键
     */
    @TableField("S_TASKALLOTPACKETID")
    private String taskAllotPacketId;

    /**
     * 派工部门表主键
     */
    @TableField("S_TASKALLOTDEPTID")
    private String taskAllotDeptId;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayPlanId;

    /**
     * 车组名称
     */
    @TableField("S_TRAINSETNAME")
    private String trainsetName;

    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    private String trainsetId;

    /**
     * 运用所CODE
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 运用所名称
     */
    @TableField("S_UNITNAME")
    private String unitName;

    /**
     * 项目CODE
     */
    @TableField("S_ITEMCODE")
    private String itemCode;

    /**
     * 项目NAME
     */
    @TableField("S_ITEMNAME")
    private String itemName;

    /**
     * 检修类型（项目类型）
     */
    @TableField("S_REPAIRTYPE")
    private String repairType;

    /**
     * 项目作业类型   1--部件  0--辆序
     */
    @TableField("S_ARRAGETYPE")
    private String arrageType;

    /**
     * 辆序
     */
    @TableField("S_CARNO")
    private String carNo;

    /**
     * 车型
     */
    @TableField("S_TRAINSETTYPE")
    private String trainsetType;

    /**
     * 计划任务ID
     */
    @TableField("S_TASKITEMID")
    private String taskItemId;

    /**
     * 修程
     */
    @TableField("S_MAINCYC")
    private String mainCyc;

    /**
     * 部件位置
     */
    @TableField("S_PARTPOSITION")
    private String partPosition;

    /**
     * 位置编码
     */
    @TableField("S_LOCATIONCODE")
    private String locationCode;

    /**
     * 位置名称
     */
    @TableField("S_LOCATIONNAME")
    private String locationName;

    /**
     * 部件名称
     */
    @TableField("S_PARTNAME")
    private String partName;

    /**
     * 数据来源
     */
    @TableField("S_DATASOURCE")
    private String dataSource;

    /**
     * 部件类型名称
     */
    @TableField("S_PARTTYPE")
    private String partType;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 检修方式  0--人工检查   1--机器人     2--机检
     */
    @TableField("S_REPAIRMODE")
    private String repairMode;

    /**
     * 记录时间
     */
    @TableField("S_RECORDTIME")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;

    /**
     * 记录人姓名
     */
    @TableField("S_RECORDERNAME")
    private String recorderName;

    /**
     * 记录人编码
     */
    @TableField("S_RECORDERCODE")
    private String recorderCode;

    /**
     * 项目唯一编码
     */
    @TableField("S_PUBLISHCODE")
    private String publishCode;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getTaskAllotPacketId() {
        return taskAllotPacketId;
    }

    public void setTaskAllotPacketId(String taskAllotPacketId) {
        this.taskAllotPacketId = taskAllotPacketId;
    }

    public String getTaskAllotDeptId() {
        return taskAllotDeptId;
    }

    public void setTaskAllotDeptId(String taskAllotDeptId) {
        this.taskAllotDeptId = taskAllotDeptId;
    }

    public String getDayPlanId() {
        return dayPlanId;
    }

    public void setDayPlanId(String dayPlanId) {
        this.dayPlanId = dayPlanId;
    }

    public String getTrainsetName() {
        return trainsetName;
    }

    public void setTrainsetName(String trainsetName) {
        this.trainsetName = trainsetName;
    }

    public String getTrainsetId() {
        return trainsetId;
    }

    public void setTrainsetId(String trainsetId) {
        this.trainsetId = trainsetId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
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

    public String getRepairType() {
        return repairType;
    }

    public void setRepairType(String repairType) {
        this.repairType = repairType;
    }

    public String getArrageType() {
        return arrageType;
    }

    public void setArrageType(String arrageType) {
        this.arrageType = arrageType;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getTrainsetType() {
        return trainsetType;
    }

    public void setTrainsetType(String trainsetType) {
        this.trainsetType = trainsetType;
    }

    public String getTaskItemId() {
        return taskItemId;
    }

    public void setTaskItemId(String taskItemId) {
        this.taskItemId = taskItemId;
    }

    public String getMainCyc() {
        return mainCyc;
    }

    public void setMainCyc(String mainCyc) {
        this.mainCyc = mainCyc;
    }

    public String getPartPosition() {
        return partPosition;
    }

    public void setPartPosition(String partPosition) {
        this.partPosition = partPosition;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getPartType() {
        return partType;
    }

    public void setPartType(String partType) {
        this.partType = partType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRepairMode() {
        return repairMode;
    }

    public void setRepairMode(String repairMode) {
        this.repairMode = repairMode;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public String getRecorderName() {
        return recorderName;
    }

    public void setRecorderName(String recorderName) {
        this.recorderName = recorderName;
    }

    public String getRecorderCode() {
        return recorderCode;
    }

    public void setRecorderCode(String recorderCode) {
        this.recorderCode = recorderCode;
    }

    public String getPublishCode() {
        return publishCode;
    }

    public void setPublishCode(String publishCode) {
        this.publishCode = publishCode;
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

    @Override
    public String toString() {
        return "TaskCarPart{" +
            "processId='" + processId + '\'' +
            ", taskAllotPacketId='" + taskAllotPacketId + '\'' +
            ", taskAllotDeptId='" + taskAllotDeptId + '\'' +
            ", dayPlanId='" + dayPlanId + '\'' +
            ", trainsetName='" + trainsetName + '\'' +
            ", trainsetId='" + trainsetId + '\'' +
            ", unitCode='" + unitCode + '\'' +
            ", unitName='" + unitName + '\'' +
            ", itemCode='" + itemCode + '\'' +
            ", itemName='" + itemName + '\'' +
            ", repairType='" + repairType + '\'' +
            ", arrageType='" + arrageType + '\'' +
            ", carNo='" + carNo + '\'' +
            ", trainsetType='" + trainsetType + '\'' +
            ", taskItemId='" + taskItemId + '\'' +
            ", mainCyc='" + mainCyc + '\'' +
            ", partPosition='" + partPosition + '\'' +
            ", locationCode='" + locationCode + '\'' +
            ", locationName='" + locationName + '\'' +
            ", partName='" + partName + '\'' +
            ", dataSource='" + dataSource + '\'' +
            ", partType='" + partType + '\'' +
            ", remark='" + remark + '\'' +
            ", repairMode='" + repairMode + '\'' +
            ", recordTime=" + recordTime +
            ", recorderName='" + recorderName + '\'' +
            ", recorderCode='" + recorderCode + '\'' +
            ", publishCode='" + publishCode + '\'' +
            '}';
    }
}
