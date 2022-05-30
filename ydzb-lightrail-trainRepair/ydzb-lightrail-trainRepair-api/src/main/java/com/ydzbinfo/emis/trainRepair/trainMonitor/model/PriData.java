package com.ydzbinfo.emis.trainRepair.trainMonitor.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/9 19:42
 **/
public class PriData {
    private String id;
    private String accuMile;
    private String joinMobileCode;
    private String jsonAccuMile;
    private String date;
    private String deptName;
    private String interTrains;
    private String bureaNameAbbr1;
    private String startStation;
    private String startTime;
    private String baseRutId;
    private String rutMobileCode;
    private String deptCode;
    private String trainMobiltCode;
    private String trainsetName;
    private String trainNo;
    private String joinTrainsetId;
    private String joinTrainsetName;
    private String trainsetId;
    List<Crew> crews;

    public String getAccuMile() {
        return accuMile;
    }

    @JSONField(name = "S_ACCUMILE")
    public void setAccuMile(String accuMile) {
        this.accuMile = accuMile;
    }

    public String getId() {
        return id;
    }
    @JSONField(name = "S_ID")
    public void setId(String id) {
        this.id = id;
    }

    public String getJoinMobileCode() {
        return joinMobileCode;
    }
    @JSONField(name = "JOIN_MOBILE_CODE")
    public void setJoinMobileCode(String joinMobileCode) {
        this.joinMobileCode = joinMobileCode;
    }

    public String getJsonAccuMile() {
        return jsonAccuMile;
    }
    @JSONField(name = "S_JIONACCUMILE")
    public void setJsonAccuMile(String jsonAccuMile) {
        this.jsonAccuMile = jsonAccuMile;
    }

    public String getDate() {
        return date;
    }
    @JSONField(name = "S_DATE")
    public void setDate(String date) {
        this.date = date;
    }

    public String getDeptName() {
        return deptName;
    }
    @JSONField(name = "S_DEPTNAME")
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getInterTrains() {
        return interTrains;
    }
    @JSONField(name = "S_INTERTRAINS")
    public void setInterTrains(String interTrains) {
        this.interTrains = interTrains;
    }

    public String getBureaNameAbbr1() {
        return bureaNameAbbr1;
    }
    @JSONField(name = "S_BUREANAMEABBR1")
    public void setBureaNameAbbr1(String bureaNameAbbr1) {
        this.bureaNameAbbr1 = bureaNameAbbr1;
    }

    public String getStartStation() {
        return startStation;
    }
    @JSONField(name = "S_STARTSTATION")
    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getStartTime() {
        return startTime;
    }
    @JSONField(name = "D_STARTTIME")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getBaseRutId() {
        return baseRutId;
    }
    @JSONField(name = "S_BASERUTID")
    public void setBaseRutId(String baseRutId) {
        this.baseRutId = baseRutId;
    }

    public String getRutMobileCode() {
        return rutMobileCode;
    }
    @JSONField(name = "RUT_MOBILE_CODE")
    public void setRutMobileCode(String rutMobileCode) {
        this.rutMobileCode = rutMobileCode;
    }

    public String getDeptCode() {
        return deptCode;
    }
    @JSONField(name = "S_DEPTCODE")
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getTrainMobiltCode() {
        return trainMobiltCode;
    }
    @JSONField(name = "TRAIN_MOBILE_CODE")
    public void setTrainMobiltCode(String trainMobiltCode) {
        this.trainMobiltCode = trainMobiltCode;
    }

    public String getTrainsetName() {
        return trainsetName;
    }
    @JSONField(name = "S_TRAINSETNAME")
    public void setTrainsetName(String trainsetName) {
        this.trainsetName = trainsetName;
    }

    public String getTrainNo() {
        return trainNo;
    }
    @JSONField(name = "S_TRAINNO")
    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public String getJoinTrainsetId() {
        return joinTrainsetId;
    }
    @JSONField(name = "S_JOINTRAINSETID")
    public void setJoinTrainsetId(String joinTrainsetId) {
        this.joinTrainsetId = joinTrainsetId;
    }

    public String getJoinTrainsetName() {
        return joinTrainsetName;
    }
    @JSONField(name = "S_JOIN_TRAINSET_NAME")
    public void setJoinTrainsetName(String joinTrainsetName) {
        this.joinTrainsetName = joinTrainsetName;
    }

    public String getTrainsetId() {
        return trainsetId;
    }
    @JSONField(name = "S_TRAINSETID")
    public void setTrainsetId(String trainsetId) {
        this.trainsetId = trainsetId;
    }

    public List<Crew> getCrews() {
        return crews;
    }

    public void setCrews(List<Crew> crews) {
        this.crews = crews;
    }
}
