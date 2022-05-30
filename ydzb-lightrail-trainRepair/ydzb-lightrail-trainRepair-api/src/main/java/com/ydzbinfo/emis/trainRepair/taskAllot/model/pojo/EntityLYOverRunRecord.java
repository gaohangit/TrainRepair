package com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author 高晗
 * @description
 * @createDate 2021/7/10 16:06
 **/
@Data
public class EntityLYOverRunRecord {
    private String id;

    /**
     * 检测单位编码
     */
    private String deptCode;

    /**
     * 检测单位
     */
    private String deptAbbr;

    /**
     * 车组号ID
     */
    private String trainsetId;

    /**
     * 车组号名称
     */
    private String trainsetName;

    /**
     * 车号
     */
    private String carNo;

    /**
     * 检测时间
     */
    private String checkTime;

    /**
     * 转向架位置
     */
    private String bogiePosition;

    /**
     * 轮对位置
     */
    private String wheelSetPosition;

    /**
     * 轮饼位置
     */
    private String wheelPosition;

    /**
     * 超限类型
     */
    private String overTypeName;

    /**
     * 超限项目
     */
    private String checkItem;

    /**
     * 超限项目名称
     */
    private String checkItemName;

    /**
     * 检测值
     */
    private String checkValue;

    /**
     * 超限等级编码
     */
    private String alarmLevel;

    /**
     * 超限等级名称
     */
    private String alarmName;

    /**
     * 复核类型
     */
    // @Value("轮对")
    private String recheckType;


    private LyReCheckResult lyReCheckResult;

    public String getId() {
        return id;
    }
    @JSONField(name = "S_ID")
    public void setId(String id) {
        this.id = id;
    }

    public String getDeptCode() {
        return deptCode;
    }
    @JSONField(name = "S_DEPTCODE")
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptAbbr() {
        return deptAbbr;
    }
    @JSONField(name = "S_DEPTABBR")
    public void setDeptAbbr(String deptAbbr) {
        this.deptAbbr = deptAbbr;
    }

    public String getTrainsetId() {
        return trainsetId;
    }
    @JSONField(name = "S_TRAINSETID")
    public void setTrainsetId(String trainsetId) {
        this.trainsetId = trainsetId;
    }

    public String getTrainsetName() {
        return trainsetName;
    }
    @JSONField(name = "S_TRAINSETNAME")
    public void setTrainsetName(String trainsetName) {
        this.trainsetName = trainsetName;
    }

    public String getCarNo() {
        return carNo;
    }
    @JSONField(name = "S_CARNO")
    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getCheckTime() {
        return checkTime;
    }
    @JSONField(name = "D_CHECKTIME")
    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getBogiePosition() {
        return bogiePosition;
    }
    @JSONField(name = "S_BOGIEPOSITION")
    public void setBogiePosition(String bogiePosition) {
        this.bogiePosition = bogiePosition;
    }

    public String getWheelSetPosition() {
        return wheelSetPosition;
    }
    @JSONField(name = "S_WHEELSETPOSITION")
    public void setWheelSetPosition(String wheelSetPosition) {
        this.wheelSetPosition = wheelSetPosition;
    }

    public String getWheelPosition() {
        return wheelPosition;
    }
    @JSONField(name = "S_WHEELPOSITION")
    public void setWheelPosition(String wheelPosition) {
        this.wheelPosition = wheelPosition;
    }

    public String getOverTypeName() {
        return overTypeName;
    }
    @JSONField(name = "S_OVERTYPENAME")
    public void setOverTypeName(String overTypeName) {
        this.overTypeName = overTypeName;
    }

    public String getCheckItem() {
        return checkItem;
    }
    @JSONField(name = "S_CHECKITEMNAME")
    public void setCheckItem(String checkItem) {
        this.checkItem = checkItem;
    }

    public String getCheckItemName() {
        return checkItemName;
    }
    @JSONField(name = "S_CHECKITEMNAME")
    public void setCheckItemName(String checkItemName) {
        this.checkItemName = checkItemName;
    }

    public String getCheckValue() {
        return checkValue;
    }
    @JSONField(name = "S_CHECKVALUE")
    public void setCheckValue(String checkValue) {
        this.checkValue = checkValue;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }
    @JSONField(name = "C_ALARMLEVEL")
    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmName() {
        return alarmName;
    }
    @JSONField(name = "C_ALARMNAME")
    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getRecheckType() {
        return recheckType;
    }

    public void setRecheckType(String recheckType) {
        this.recheckType = recheckType;
    }

    public LyReCheckResult getLyReCheckResult() {
        return lyReCheckResult;
    }
    @JSONField(name = "ReCheckResult")
    public void setLyReCheckResult(LyReCheckResult lyReCheckResult) {
        this.lyReCheckResult = lyReCheckResult;
    }
}
