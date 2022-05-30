package com.ydzbinfo.emis.trainRepair.remotemodel.trainuse;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

/**
 * @author 高晗
 * @description 调车计划
 * @createDate 2021/7/1 9:55
 **/
public class ShuntPlanModel {

    private String trackPositionCode;
    private String isMaster;
    private String headDirection;
    private Date inTime;
    private String shuntNodeId;
    // TODO 原始数据为对象，并非列表，因异常数据（具体形式遗忘，svn历史因出差武汉而无法获得），由高晗变更为列表，使用了fastjson能把对象转列表的能力
    private List<RunningInfoModel> backRunningInfo;
    // TODO 同上
    private List<RunningInfoModel> outRunningInfo;
    private String trackCode;
    private String isAuxIliAry;
    private String emuId;
    private String trackName;
    private Date createDate;
    private String relatedNodeId;
    private String isResolve;
    private String depotCode;
    private Date outTime;
    private String relatedEmuId;
    private String relateTrainsetName;

    private String trackPositionName;
    private String remark;
    private String libCode;
    private String comPositionFlag;
    private String trackAreaCode;
    private String trackAreaName;

    private String trainsetName;

    public String getRelateTrainsetName() {
        return relateTrainsetName;
    }

    public void setRelateTrainsetName(String relateTrainsetName) {
        this.relateTrainsetName = relateTrainsetName;
    }

    public String getTrainsetName() {
        return trainsetName;
    }

    public void setTrainsetName(String trainsetName) {
        this.trainsetName = trainsetName;
    }

    public String getTrackAreaCode() {
        return trackAreaCode;
    }

    @JSONField(name = "I_TRACKAREACODE")
    public void setTrackAreaCode(String trackAreaCode) {
        this.trackAreaCode = trackAreaCode;
    }

    public String getTrackAreaName() {
        return trackAreaName;
    }

    @JSONField(name = "S_TRACKAREANAME")
    public void setTrackAreaName(String trackAreaName) {
        this.trackAreaName = trackAreaName;
    }

    public String getTrackPositionCode() {
        return trackPositionCode;
    }

    @JSONField(name = "TRACK_POSITION_CODE")
    public void setTrackPositionCode(String trackPositionCode) {
        this.trackPositionCode = trackPositionCode;
    }

    public String getIsMaster() {
        return isMaster;
    }

    @JSONField(name = "IS_MASTER")
    public void setIsMaster(String isMaster) {
        this.isMaster = isMaster;
    }

    public String getHeadDirection() {
        return headDirection;
    }

    @JSONField(name = "HEAD_DIRECTION")
    public void setHeadDirection(String headDirection) {
        this.headDirection = headDirection;
    }

    public Date getInTime() {
        return inTime;
    }

    @JSONField(name = "IN_TIME")
    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public String getShuntNodeId() {
        return shuntNodeId;
    }

    @JSONField(name = "SHUNT_NODE_ID")
    public void setShuntNodeId(String shuntNodeId) {
        this.shuntNodeId = shuntNodeId;
    }

    public List<RunningInfoModel> getBackRunningInfo() {
        return backRunningInfo;
    }

    public void setBackRunningInfo(List<RunningInfoModel> backRunningInfo) {
        this.backRunningInfo = backRunningInfo;
    }

    public List<RunningInfoModel> getOutRunningInfo() {
        return outRunningInfo;
    }

    public void setOutRunningInfo(List<RunningInfoModel> outRunningInfo) {
        this.outRunningInfo = outRunningInfo;
    }

    public String getTrackCode() {
        return trackCode;
    }

    @JSONField(name = "TRACK_CODE")
    public void setTrackCode(String trackCode) {
        this.trackCode = trackCode;
    }

    public String getIsAuxIliAry() {
        return isAuxIliAry;
    }

    @JSONField(name = "IS_AUXILIARY")
    public void setIsAuxIliAry(String isAuxIliAry) {
        this.isAuxIliAry = isAuxIliAry;
    }

    public String getEmuId() {
        return emuId;
    }

    @JSONField(name = "EMU_ID")
    public void setEmuId(String emuId) {
        this.emuId = emuId;
    }

    public String getTrackName() {
        return trackName;
    }

    @JSONField(name = "TRACK_NAME")
    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    @JSONField(name = "CREATE_DATE")
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getRelatedNodeId() {
        return relatedNodeId;
    }

    @JSONField(name = "RELATED_NODE_ID")
    public void setRelatedNodeId(String relatedNodeId) {
        this.relatedNodeId = relatedNodeId;
    }

    public String getIsResolve() {
        return isResolve;
    }

    @JSONField(name = "IS_RESOLVE")
    public void setIsResolve(String isResolve) {
        this.isResolve = isResolve;
    }

    public String getDepotCode() {
        return depotCode;
    }

    @JSONField(name = "DEPOT_CODE")
    public void setDepotCode(String depotCode) {
        this.depotCode = depotCode;
    }

    public Date getOutTime() {
        return outTime;
    }

    @JSONField(name = "OUT_TIME")
    public void setOutTime(Date outTime) {
        this.outTime = outTime;
    }

    public String getRelatedEmuId() {
        return relatedEmuId;
    }

    @JSONField(name = "RELATED_EMU_ID")
    public void setRelatedEmuId(String relatedEmuId) {
        this.relatedEmuId = relatedEmuId;
    }

    public String getTrackPositionName() {
        return trackPositionName;
    }

    @JSONField(name = "TRACK_POSITION_NAME")
    public void setTrackPositionName(String trackPositionName) {
        this.trackPositionName = trackPositionName;
    }

    public String getRemark() {
        return remark;
    }

    @JSONField(name = "REMARK")
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLibCode() {
        return libCode;
    }

    @JSONField(name = "LIB_CODE")
    public void setLibCode(String libCode) {
        this.libCode = libCode;
    }

    public String getComPositionFlag() {
        return comPositionFlag;
    }

    @JSONField(name = "COMPOSITION_FLAG")
    public void setComPositionFlag(String comPositionFlag) {
        this.comPositionFlag = comPositionFlag;
    }
}
