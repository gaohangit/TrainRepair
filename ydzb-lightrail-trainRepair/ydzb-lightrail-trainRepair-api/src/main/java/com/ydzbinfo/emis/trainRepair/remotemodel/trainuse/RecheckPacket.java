package com.ydzbinfo.emis.trainRepair.remotemodel.trainuse;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author 高晗
 * @description
 * @createDate 2021/8/31 14:18
 **/
public class RecheckPacket {
    /**
     *主键/作业包编码
     */
    private String packetCode;

    /**
     *业务分类编码
     */
    private String busType;

    /**
     *适用车型
     */
    private String  suitModel;

    /**
     *作业包分类编码(作业包类型)
     */
    private String packetType;

    /**
     *排序
     */
    private String sort;

    /**
     *作业包名称
     */
    private String packetName;

    /**
     *是否可用（1：可用，0：停用）
     */
    private String usedFlag;

    /**
     *父包编码
     */
    private String fatherCode;

    /**
     *单位类型
     */
    private String unitType;

    /**
     * 下发标志（0：未下发，1：已下发）
     */
    private String issueFlag;

    /**
     * 检修类型(Y:预防性，G：更正性)
     */
    private String repairType;

    /**
     * 修程编码
     */
    private String repairCode;

    /**
     *单位编码
     */
    private String unitCode;

    /**
     *适用批次
     */
    private String suitBatch;

    /**
     * 检修模式（0：均衡修，1：集中修）
     */
    private String repairMode;

    /**
     *下发时间
     */
    private String issueDate;

    public String getPacketCode() {
        return packetCode;
    }

    @JSONField(name = "sPacketCode")
    public void setPacketCode(String packetCode) {
        this.packetCode = packetCode;
    }

    public String getBusType() {
        return busType;
    }

    @JSONField(name = "sBusType")
    public void setBusType(String busType) {
        this.busType = busType;
    }

    public String getSuitModel() {
        return suitModel;
    }

    @JSONField(name = "suitModel")
    public void setSuitModel(String suitModel) {
        this.suitModel = suitModel;
    }

    public String getPacketType() {
        return packetType;
    }

    @JSONField(name = "sPacketType")
    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }

    public String getSort() {
        return sort;
    }

    @JSONField(name = "iSort")
    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getPacketName() {
        return packetName;
    }

    @JSONField(name = "sPacketName")
    public void setPacketName(String packetName) {
        this.packetName = packetName;
    }

    public String getUsedFlag() {
        return usedFlag;
    }

    @JSONField(name = "cUsedFlag")
    public void setUsedFlag(String usedFlag) {
        this.usedFlag = usedFlag;
    }

    public String getFatherCode() {
        return fatherCode;
    }

    @JSONField(name = "sFatherCode")
    public void setFatherCode(String fatherCode) {
        this.fatherCode = fatherCode;
    }

    public String getUnitType() {
        return unitType;
    }

    @JSONField(name = "sUnitType")
    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getIssueFlag() {
        return issueFlag;
    }

    @JSONField(name = "cIssueFlag")
    public void setIssueFlag(String issueFlag) {
        this.issueFlag = issueFlag;
    }


    public String getRepairType() {
        return repairType;
    }

    @JSONField(name = "cRepairType")
    public void setRepairType(String repairType) {
        this.repairType = repairType;
    }

    public String getRepairCode() {
        return repairCode;
    }
    @JSONField(name = "sRepairCode")
    public void setRepairCode(String repairCode) {
        this.repairCode = repairCode;
    }

    public String getUnitCode() {
        return unitCode;
    }

    @JSONField(name = "sUnitCode")
    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getSuitBatch() {
        return suitBatch;
    }

    @JSONField(name = "suitBatch")
    public void setSuitBatch(String suitBatch) {
        this.suitBatch = suitBatch;
    }


    public String getRepairMode() {
        return repairMode;
    }

    @JSONField(name = "cRepairMode")
    public void setRepairMode(String repairMode) {
        this.repairMode = repairMode;
    }

    public String getIssueDate() {
        return issueDate;
    }

    @JSONField(name = "dIssueDate")
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }
}
