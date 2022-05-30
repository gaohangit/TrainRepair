package com.ydzbinfo.emis.trainRepair.trainMonitor.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * @author gaohan
 * @description 检修信息(运用修)
 * @createDate 2021/3/9 11:22
 **/
public class UnitLevelRepairInfo {
    //车组号名称
    private String  trainsetName;

    //包编码
    private String  packetCode;

    //上一次检修里程
    private String repairMileage;

    //主键ID

    private String  packetRecordId;


    //包名，作业包名称
    private String  packetName;

    //修程编码
    private String mainCycCode;

    //最早检修时间/修俊时间
    private String  repairTime;

    //上一次检修距当前天数差
    private String dayDifference;


    //上一次检修距当前里程差
    private String mileageDifference;


    //累计走行公里
    private String accuMile;

    private List<HighLevelRepairInfo> highLevelRepairInfos;

    public String getTrainsetName() {
        return trainsetName;
    }
    @JSONField(name="S_TRAINSETNAME")
    public void setTrainsetName(String trainsetName) {
        this.trainsetName = trainsetName;
    }

    public String getPacketCode() {
        return packetCode;
    }
    @JSONField(name="S_SPPACKETCODE")
    public void setPacketCode(String packetCode) {
        this.packetCode = packetCode;
    }

    public String getRepairMileage() {
        return repairMileage;
    }
    @JSONField(name="REPAIRMILE")
    public void setRepairMileage(String repairMileage) {
        this.repairMileage = repairMileage;
    }

    public String getPacketRecordId() {
        return packetRecordId;
    }
    @JSONField(name="S_PACKETRECORDID")
    public void setPacketRecordId(String packetRecordId) {
        this.packetRecordId = packetRecordId;
    }

    public String getPacketName() {
        return packetName;
    }
    @JSONField(name="S_SPPACKETNAME")
    public void setPacketName(String packetName) {
        this.packetName = packetName;
    }

    public String getMainCycCode() {
        return mainCycCode;
    }
    @JSONField(name="S_MAINTCYCCODE")
    public void setMainCycCode(String mainCycCode) {
        this.mainCycCode = mainCycCode;
    }

    public String getRepairTime() {
        return repairTime;
    }

    public void setRepairTime(String repairTime) {
        this.repairTime = repairTime;
    }

    public String getDayDifference() {
        return dayDifference;
    }

    public void setDayDifference(String dayDifference) {
        this.dayDifference = dayDifference;
    }

    public String getMileageDifference() {
        return mileageDifference;
    }

    public void setMileageDifference(String mileageDifference) {
        this.mileageDifference = mileageDifference;
    }

    public String getAccuMile() {
        return accuMile;
    }

    public void setAccuMile(String accuMile) {
        this.accuMile = accuMile;
    }
}
