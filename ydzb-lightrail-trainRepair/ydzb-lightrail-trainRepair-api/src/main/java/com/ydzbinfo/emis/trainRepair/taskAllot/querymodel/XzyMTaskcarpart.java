package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-11
 */
public class XzyMTaskcarpart extends Model<XzyMTaskcarpart> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String processId;
    /**
     * 日计划ID
     */
    private String dayPlanID;
    /**
     * 车组名称
     */
    private String trainsetName;
    /**
     * 车组ID
     */
    private String trainsetId;
    /**
     * 运用所CODE
     */
    private String unitCode;
    /**
     * 运用所名称
     */
    private String unitName;
    /**
     * 项目CODE
     */
    private String ItemCode;
    /**
     * 项目NAME
     */
    private String ItemName;
    /**
     * 检修类型（项目类型）
     */
    private String repairType;
    /**
     * 项目作业类型   1--部件  0--辆序
     */
    private String arrageType;
    /**
     * 数据来源
     */
    private String dataSource;
    /**
     * 部件类型名称
     */
    private String partType;
    /**
     * 部件位置
     */
    private String partPosition;

    /***
     * 位置编码
     */
    private String locationCode;

    /***
     * 位置名称
     */
    private String locationName;


    /**
     * 备注
     */
    private String remark;
    /**
     * 检修方式  0--人工检查   1--机器人     2--机检
     */
    private String repairMode;
    /**
     * 部件名称
     */
    private String partName;
    /**
     * 记录时间
     */
    private Date recordTime;
    /**
     * 记录人姓名
     */
    private String recorderName;
    /**
     * 记录人编码
     */
    private String recorderCode;
    /**
     * 派工包表主键
     */
    private String taskAllotPacketId;
    /**
     * 派工部门表主键
     */
    private String taskAllotDeptId;
    /**
     * 辆序
     */
    private String carNo;
    /**
     * 车型
     */
    private String trainsetType;
    /**
     * 计划任务ID
     */
    private String taskItemId;

    /**
     * 派工包Name
     */
    private String packetName;

    /**
     * 派工包Code
     */
    private String packetCode;

    /**
     * 派工包Type
     */
    private String packetType;

    /**
     * 任务包ID
     */
    private String taskId;

    /**
     * 修程
     */
    private String mainCyc;

    /**
     * 派工部门
     */
    private XzyMTaskallotdept xzyMTaskallotdept = new XzyMTaskallotdept();

    /**
     * 派工人员集合
     */
    private List<XzyMTaskallotperson> workerList = new ArrayList<>();

    /**
     * 显示辆序部件名称
     */
    private String displayCarPartName;

    /**
     * 是否可以开始 1是 2否
     */
    private String isBegin;

    public String getIsBegin() {
        return isBegin;
    }

    public void setIsBegin(String isBegin) {
        this.isBegin = isBegin;
    }

    public String getDisplayCarPartName() {
        return displayCarPartName;
    }

    public void setDisplayCarPartName(String displayCarPartName) {
        this.displayCarPartName = displayCarPartName;
    }

    public List<XzyMTaskallotperson> getWorkerList() {
        return workerList;
    }

    public void setWorkerList(List<XzyMTaskallotperson> workerList) {
        this.workerList = workerList;
    }

    public XzyMTaskallotdept getXzyMTaskallotdept() {
        return xzyMTaskallotdept;
    }

    public void setXzyMTaskallotdept(XzyMTaskallotdept xzyMTaskallotdept) {
        this.xzyMTaskallotdept = xzyMTaskallotdept;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getDayPlanID() {
        return dayPlanID;
    }

    public void setDayPlanID(String dayPlanID) {
        this.dayPlanID = dayPlanID;
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
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
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

    public String getPartPosition() {
        return partPosition;
    }

    public void setPartPosition(String partPosition) {
        this.partPosition = partPosition;
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

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPacketName() {
        return packetName;
    }

    public void setPacketName(String packetName) {
        this.packetName = packetName;
    }

    public String getPacketCode() {
        return packetCode;
    }

    public void setPacketCode(String packetCode) {
        this.packetCode = packetCode;
    }

    public String getPacketType() {
        return packetType;
    }

    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getMainCyc() {
        return mainCyc;
    }

    public void setMainCyc(String mainCyc) {
        this.mainCyc = mainCyc;
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
        return "XzyMTaskcarpart{" +
                "processId='" + processId + '\'' +
                ", dayPlanID='" + dayPlanID + '\'' +
                ", trainsetName='" + trainsetName + '\'' +
                ", trainsetId='" + trainsetId + '\'' +
                ", unitCode='" + unitCode + '\'' +
                ", unitName='" + unitName + '\'' +
                ", ItemCode='" + ItemCode + '\'' +
                ", ItemName='" + ItemName + '\'' +
                ", repairType='" + repairType + '\'' +
                ", arrageType='" + arrageType + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", partType='" + partType + '\'' +
                ", partPosition='" + partPosition + '\'' +
                ", remark='" + remark + '\'' +
                ", repairMode='" + repairMode + '\'' +
                ", partName='" + partName + '\'' +
                ", recordTime=" + recordTime +
                ", recorderName='" + recorderName + '\'' +
                ", recorderCode='" + recorderCode + '\'' +
                ", taskAllotPacketId='" + taskAllotPacketId + '\'' +
                ", taskAllotDeptId='" + taskAllotDeptId + '\'' +
                ", carNo='" + carNo + '\'' +
                ", trainsetType='" + trainsetType + '\'' +
                ", taskItemId='" + taskItemId + '\'' +
                ", packetName='" + packetName + '\'' +
                ", packetCode='" + packetCode + '\'' +
                ", packetType='" + packetType + '\'' +
                ", taskId='" + taskId + '\'' +
                ", mainCyc='" + mainCyc + '\'' +
                '}';
    }

    @Override
    protected Serializable pkVal() {
        return this.processId;
    }
}
