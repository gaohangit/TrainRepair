package com.ydzbinfo.emis.trainRepair.remotemodel.fault;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author 高晗
 * @description
 * @createDate 2021/8/30 14:34
 **/
@Data
public class FaultPacket {
    /**
     * 修程编码
     */
    private String repairCode;

    /**
     * 下发标志
     */
    private String issueFlag;

    /**
     * 排序
     */
    private String sort;

    /**
     * 作业包分类编码
     */
    private String packetType;

    /**
     * 单位编码
     */
    private String unitCode;

    /**
     * 适用批次
     */
    private String suitBatch;

    /**
     *  父包编码
     */
    private String  fatherCode;

    /**
     * 业务分类编码
     */
    private String unitType;

    /**
     * 作业包类型名称
     */
    private String  packetTypeName;

    /**
     * 下发时间
     */
    private String issueDate;

    /**
     *检修类型(Y:预防性，G：更正性)
     */
    private String repairType;

    /**
     *是否可用（1：可用，0：停用）
     */
    private String usedFlag;

    private String id;

    /**
     *作业包名称
     */
    private String packetName;

    /**
     *主键/作业包编码
     */
    private String packetCode;

    /**
     *业务分类编码
     */
    private String busType;

    /**
     *检修模式（0：均衡修，1：集中修）
     */
    private String repairMode;

    /**
     *适用车型
     */
    private String suitModel;

    public String getRepairCode() {
        return repairCode;
    }

    @JSONField(name = "S_REPAIR_CODE")
    public void setRepairCode(String repairCode) {
        this.repairCode = repairCode;
    }

    public String getIssueFlag() {
        return issueFlag;
    }

    @JSONField(name = "C_ISSUE_FLAG")
    public void setIssueFlag(String issueFlag) {
        this.issueFlag = issueFlag;
    }

    public String getSort() {
        return sort;
    }

    @JSONField(name = "I_SORT")
    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getPacketType() {
        return packetType;
    }

    @JSONField(name = "S_PACKET_TYPE")
    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }

    public String getUnitCode() {
        return unitCode;
    }

    @JSONField(name = "S_UNIT_CODE")
    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getSuitBatch() {
        return suitBatch;
    }

    @JSONField(name = "SUIT_BATCH")
    public void setSuitBatch(String suitBatch) {
        this.suitBatch = suitBatch;
    }

    public String getFatherCode() {
        return fatherCode;
    }

    @JSONField(name = "S_FATHER_CODE")
    public void setFatherCode(String fatherCode) {
        this.fatherCode = fatherCode;
    }

    public String getUnitType() {
        return unitType;
    }

    @JSONField(name = "S_UNIT_TYPE")
    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getPacketTypeName() {
        return packetTypeName;
    }

    @JSONField(name = "S_PACKETTYPENAME")
    public void setPacketTypeName(String packetTypeName) {
        this.packetTypeName = packetTypeName;
    }

    public String getIssueDate() {
        return issueDate;
    }

    @JSONField(name = "D_ISSUE_DATE")
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getRepairType() {
        return repairType;
    }

    @JSONField(name = "C_REPAIR_TYPE")
    public void setRepairType(String repairType) {
        this.repairType = repairType;
    }

    public String getUsedFlag() {
        return usedFlag;
    }

    @JSONField(name = "C_USED_FLAG")
    public void setUsedFlag(String usedFlag) {
        this.usedFlag = usedFlag;
    }

    public String getId() {
        return id;
    }

    @JSONField(name = "ID")
    public void setId(String id) {
        this.id = id;
    }

    public String getPacketName() {
        return packetName;
    }

    @JSONField(name = "S_PACKET_NAME")
    public void setPacketName(String packetName) {
        this.packetName = packetName;
    }

    public String getPacketCode() {
        return packetCode;
    }

    @JSONField(name = "S_PACKET_CODE")
    public void setPacketCode(String packetCode) {
        this.packetCode = packetCode;
    }

    public String getBusType() {
        return busType;
    }

    @JSONField(name = "S_BUS_TYPE")
    public void setBusType(String busType) {
        this.busType = busType;
    }

    public String getRepairMode() {
        return repairMode;
    }

    @JSONField(name = "C_REPAIR_MODE")
    public void setRepairMode(String repairMode) {
        this.repairMode = repairMode;
    }

    public String getSuitModel() {
        return suitModel;
    }

    @JSONField(name = "SUIT_MODEL")
    public void setSuitModel(String suitModel) {
        this.suitModel = suitModel;
    }
}
