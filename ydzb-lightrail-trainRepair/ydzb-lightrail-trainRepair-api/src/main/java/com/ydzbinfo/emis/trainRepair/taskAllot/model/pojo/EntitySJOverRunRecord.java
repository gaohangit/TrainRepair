/**
 * Copyright 2021 bejson.com
 */
package com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Auto-generated: 2021-03-24 16:54:41
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class EntitySJOverRunRecord {

    private String id;

    /**
     * 车组号ID
     */
    private String trainsetId;

    /**
     * 车组号名称
     */
    private String trainsetName;

    /**
     *受电弓编号
     */
    private String pantoCode;

    /**
     *滑板编号
     */
    private String skaCode;

    /**
     *检测时间
     */
    private String checkTime;

    /**
     *超限项目编码
     */
    private String checkItem;

    /**
     *超限项目名称
     */
    private String checkItemName;

    /**
     *检测值
     */
    private String checkValue;

    /**
     *检测项目编码
     */
    private String checkCode;

    /**
     *厂家检测项目编码
     */
    private String checkCodeZd;

    /**
     *分析值
     */
    private String analyseValue;

    /**
     *分析意见
     */
    private String analyseLevel;

    /**
     * 报警级别,参见状态字典
     */
    private String alarmLevel;

    /**
     * 超限等级名称
     */
    private String alarmName;

    /**
     * 合格上限
     */
    private String maxEligible;

    /**
     *合格下限
     */
    private String mineLegible;
    /**
     * 复核结果
     */

    private SjReCheckResult sjReCheckResult;

    private String carNo;

    /**
     * 复核类型
     */
    private String recheckType="受电弓";

    public String getId() {
        return id;
    }
    @JSONField(name = "S_ID")
    public void setId(String id) {
        this.id = id;
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

    public String getPantoCode() {
        return pantoCode;
    }
    @JSONField(name = "S_PANTOCODE")
    public void setPantoCode(String pantoCode) {
        this.pantoCode = pantoCode;
    }

    public String getSkaCode() {
        return skaCode;
    }
    @JSONField(name = "S_SKACODE")
    public void setSkaCode(String skaCode) {
        this.skaCode = skaCode;
    }

    public String getCheckTime() {
        return checkTime;
    }
    @JSONField(name = "D_CHECKTIME")
    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getCheckItem() {
        return checkItem;
    }
    @JSONField(name = "C_CHECKITEM")
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

    public String getCheckCode() {
        return checkCode;
    }
    @JSONField(name = "S_CHECKCODE")
    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getCheckCodeZd() {
        return checkCodeZd;
    }
    @JSONField(name = "S_CHECKCODEZD")
    public void setCheckCodeZd(String checkCodeZd) {
        this.checkCodeZd = checkCodeZd;
    }

    public String getAnalyseValue() {
        return analyseValue;
    }
    @JSONField(name = "S_ANALYSEVALUE")
    public void setAnalyseValue(String analyseValue) {
        this.analyseValue = analyseValue;
    }

    public String getAnalyseLevel() {
        return analyseLevel;
    }
    @JSONField(name = "S_ANALYSELEVEL")
    public void setAnalyseLevel(String analyseLevel) {
        this.analyseLevel = analyseLevel;
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

    public String getMaxEligible() {
        return maxEligible;
    }
    @JSONField(name = "S_MAXELIGIBLE")
    public void setMaxEligible(String maxEligible) {
        this.maxEligible = maxEligible;
    }

    public String getMineLegible() {
        return mineLegible;
    }
    @JSONField(name = "S_MINELEGIBLE")
    public void setMineLegible(String mineLegible) {
        this.mineLegible = mineLegible;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getRecheckType() {
        return recheckType;
    }

    public void setRecheckType(String recheckType) {
        this.recheckType = recheckType;
    }

    public SjReCheckResult getSjReCheckResult() {
        return sjReCheckResult;
    }
    @JSONField(name = "ReCheckResult")
    public void setSjReCheckResult(SjReCheckResult sjReCheckResult) {
        this.sjReCheckResult = sjReCheckResult;
    }
}