/**
 * Copyright 2021 bejson.com
 */
package com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Auto-generated: 2021-03-24 16:54:41
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class LyReCheckResult {
    /**
     * 超限主键ID
     */
    private String id;

    /**
     * 是否复核(0否,1是)
     */
    private String isRecheck;

    /**
     * 复核时间
     */
    private String revTime;

    /**
     * 复核后是否超限(0不超限，1超限')
     */
    private String whsCheckPerson;

    /**
     * 复核值
     */
    private String recheckValue;
    /**
     * 复核人（班组 人）
     */
    private String recheckLevel;
    /**
     * 复核结果
     */
    private String overState;

    /**
     * 处理人
     */
    private String proPerson;

    /**
     * 复核意见
     */
    private String revCase;

    public String getId() {
        return id;
    }

    @JSONField(name = "S_ID")
    public void setId(String id) {
        this.id = id;
    }

    public String getIsRecheck() {
        return isRecheck;
    }

    @JSONField(name = "C_ISRECHECK")
    public void setIsRecheck(String isRecheck) {
        this.isRecheck = isRecheck;
    }

    public String getRecheckValue() {
        return recheckValue;
    }

    @JSONField(name = "S_RECHECKVALUE")
    public void setRecheckValue(String recheckValue) {
        this.recheckValue = recheckValue;
    }

    public String getRecheckLevel() {
        return recheckLevel;
    }

    @JSONField(name = "S_RECHECKLEVEL")
    public void setRecheckLevel(String recheckLevel) {
        this.recheckLevel = recheckLevel;
    }


    public String getOverState() {
        return overState;
    }

    @JSONField(name = "C_OVERSTATE")
    public void setOverState(String overState) {
        this.overState = overState;
    }

    public String getWhsCheckPerson() {
        return whsCheckPerson;
    }

    @JSONField(name = "S_WHSCHECKPERSON")
    public void setWhsCheckPerson(String whsCheckPerson) {
        this.whsCheckPerson = whsCheckPerson;
    }

    public String getProPerson() {
        return proPerson;
    }

    @JSONField(name = "S_PROPERSON")
    public void setProPerson(String proPerson) {
        this.proPerson = proPerson;
    }

    public String getRevTime() {
        return revTime;
    }

    @JSONField(name = "D_REVTIME")
    public void setRevTime(String revTime) {
        this.revTime = revTime;
    }

    public String getRevCase() {
        return revCase;
    }

    @JSONField(name = "S_REVCASE")
    public void setRevCase(String revCase) {
        this.revCase = revCase;
    }
}