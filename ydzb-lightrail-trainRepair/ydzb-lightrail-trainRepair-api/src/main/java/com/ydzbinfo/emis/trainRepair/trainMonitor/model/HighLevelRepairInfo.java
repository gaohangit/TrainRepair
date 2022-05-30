package com.ydzbinfo.emis.trainRepair.trainMonitor.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

/**
 * @author 高晗
 * @description 检修信息(高级修)
 * @createDate 2021/12/20 16:13
 **/
@ToString
public class HighLevelRepairInfo {
    private String trainsetId;

    private String trainsetType;

    /**
     * 检修单位
     */
    private String repairDeptName;

    /**
     * 修程
     */
    private String level;

    /**
     * 高级修时走行公里
     */

    private String accuMile;

    /**
     * 修竣时间
     */
    private String time;

    public String getTrainsetType() {
        return trainsetType;
    }

    @JSONField(name = "S_TRAINSETTYPE2")
    public void setTrainsetType(String trainsetType) {
        this.trainsetType = trainsetType;
    }

    public String getRepairDeptName() {
        return repairDeptName;
    }

    @JSONField(name = "S_REPAIRDEPTNAME")
    public void setRepairDeptName(String repairDeptName) {
        this.repairDeptName = repairDeptName;
    }

    public String getLevel() {
        return level;
    }

    @JSONField(name = "I_MLEVEL")
    public void setLevel(String level) {
        this.level = level;
    }

    public String getAccuMile() {
        return accuMile;
    }

    @JSONField(name = "I_CYCACCUMILE")
    public void setAccuMile(String accuMile) {
        this.accuMile = accuMile;
    }

    public String getTrainsetId() {
        return trainsetId;
    }

    @JSONField(name = "S_TRAINSETID")
    public void setTrainsetId(String trainsetId) {
        this.trainsetId = trainsetId;
    }

    public String getTime() {
        return time;
    }

    @JSONField(name = "D_CYCTIME")
    public void setTime(String time) {
        this.time = time;
    }

}
