package com.ydzbinfo.emis.trainRepair.remotemodel.resume;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author 高晗
 * @description
 * @createDate 2021/7/7 9:52
 **/
public class TrainsetHotSpareInfo {
    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 配属局
     */
    private String buReaNameAbbr;

    /**
     * 值乘人员
     */
    private String stuffName;

    /**
     * 走行公里
     */
    private String accuMile;

    /**
     * 热备地点
     */
    private String reBeiPlace;

    /**
     * 热备线路
     */
    private String reBeiLine;


    /**
     * 备注
     */
    private String remark;

    public String getReBeiPlace() {
        return reBeiPlace;
    }
    @JSONField(name = "rebeiplace")
    public void setReBeiPlace(String reBeiPlace) {
        this.reBeiPlace = reBeiPlace;
    }

    public String getReBeiLine() {
        return reBeiLine;
    }

    @JSONField(name = "rebeiline")
    public void setReBeiLine(String reBeiLine) {
        this.reBeiLine = reBeiLine;
    }

    public String getAccuMile() {
        return accuMile;
    }
    @JSONField(name = "s_accumile")
    public void setAccuMile(String accuMile) {
        this.accuMile = accuMile;
    }

    public String getBuReaNameAbbr() {
        return buReaNameAbbr;
    }
    @JSONField(name = "s_bureanameabbr1")
    public void setBuReaNameAbbr(String buReaNameAbbr) {
        this.buReaNameAbbr = buReaNameAbbr;
    }

    public String getStuffName() {
        return stuffName;
    }
    @JSONField(name = "s_stuffname")
    public void setStuffName(String stuffName) {
        this.stuffName = stuffName;
    }

    public String getRemark() {
        return remark;
    }
    @JSONField(name = "s_remark")
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTrainsetName() {
        return trainsetName;
    }
    @JSONField(name = "s_trainsetname")
    public void setTrainsetName(String trainsetName) {
        this.trainsetName = trainsetName;
    }
}
