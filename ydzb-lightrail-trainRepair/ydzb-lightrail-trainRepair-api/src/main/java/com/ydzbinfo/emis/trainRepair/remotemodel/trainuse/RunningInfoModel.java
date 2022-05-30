package com.ydzbinfo.emis.trainRepair.remotemodel.trainuse;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * @author 高晗
 * @description
 * @createDate 2021/7/1 16:00
 **/
public class RunningInfoModel {
    private String inTime;
    private String shuntNodeId;
    private String depotCode;
    private String baseRutId;
    private String detailRutId;
    private Date outTime;
    private String id;
    private String flag;
    private String inOutTime;
    private String libCode;
    private String trainNo;

    public String getInTime() {
        return inTime;
    }
    @JSONField(name = "IN_TIME")
    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getShuntNodeId() {
        return shuntNodeId;
    }
    @JSONField(name = "SHUNT_NODE_ID")
    public void setShuntNodeId(String shuntNodeId) {
        this.shuntNodeId = shuntNodeId;
    }

    public String getDepotCode() {
        return depotCode;
    }
    @JSONField(name = "DEPOT_CODE")
    public void setDepotCode(String depotCode) {
        this.depotCode = depotCode;
    }

    public String getBaseRutId() {
        return baseRutId;
    }
    @JSONField(name = "BASE_RUT_ID")
    public void setBaseRutId(String baseRutId) {
        this.baseRutId = baseRutId;
    }

    public String getDetailRutId() {
        return detailRutId;
    }
    @JSONField(name = "DETAIL_RUT_ID")
    public void setDetailRutId(String detailRutId) {
        this.detailRutId = detailRutId;
    }

    public Date getOutTime() {
        return outTime;
    }
    @JSONField(name = "OUT_TIME")
    public void setOutTime(Date outTime) {
        this.outTime = outTime;
    }

    public String getId() {
        return id;
    }
    @JSONField(name = "ID")
    public void setId(String id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }
    @JSONField(name = "FLAG")
    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getInOutTime() {
        return inOutTime;
    }
    @JSONField(name = "IN_OUT_TIME")
    public void setInOutTime(String inOutTime) {
        this.inOutTime = inOutTime;
    }

    public String getLibCode() {
        return libCode;
    }
    @JSONField(name = "LIB_CODE")
    public void setLibCode(String libCode) {
        this.libCode = libCode;
    }

    public String getTrainNo() {
        return trainNo;
    }
    @JSONField(name = "TRAIN_NO")
    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }
}
