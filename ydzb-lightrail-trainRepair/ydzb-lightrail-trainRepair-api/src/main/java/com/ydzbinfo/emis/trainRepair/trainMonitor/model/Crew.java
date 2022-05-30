package com.ydzbinfo.emis.trainRepair.trainMonitor.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/9 19:45
 **/
public class Crew {
    private String startTime;
    private String endTime;
    private String trainNo;
    private String dutyName;
    private String stuffMobile;
    private String trainsetId;
    private String orderDir;
    private String time;
    private String stuffCode;
    private String stuffName;

    public String getStartTime() {
        return startTime;
    }
    @JSONField(name = "S_STARTTIME")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }
    @JSONField(name = "S_ENDTIME")
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTrainNo() {
        return trainNo;
    }
    @JSONField(name = "S_TRAINNO")
    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public String getDutyName() {
        return dutyName;
    }
    @JSONField(name = "S_DUTYNAME")
    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    public String getStuffMobile() {
        return stuffMobile;
    }
    @JSONField(name = "S_STUFFMOBILE")
    public void setStuffMobile(String stuffMobile) {
        this.stuffMobile = stuffMobile;
    }

    public String getTrainsetId() {
        return trainsetId;
    }
    @JSONField(name = "S_TRAINSETID")
    public void setTrainsetId(String trainsetId) {
        this.trainsetId = trainsetId;
    }

    public String getOrderDir() {
        return orderDir;
    }
    @JSONField(name = "I_ORDERDIR")
    public void setOrderDir(String orderDir) {
        this.orderDir = orderDir;
    }

    public String getTime() {
        return time;
    }
    @JSONField(name = "time")
    public void setTime(String time) {
        this.time = time;
    }

    public String getStuffCode() {
        return stuffCode;
    }
    @JSONField(name = "S_STUFFCODE")
    public void setStuffCode(String stuffCode) {
        this.stuffCode = stuffCode;
    }

    public String getStuffName() {
        return stuffName;
    }
    @JSONField(name = "S_STUFFNAME")
    public void setStuffName(String stuffName) {
        this.stuffName = stuffName;
    }
}
