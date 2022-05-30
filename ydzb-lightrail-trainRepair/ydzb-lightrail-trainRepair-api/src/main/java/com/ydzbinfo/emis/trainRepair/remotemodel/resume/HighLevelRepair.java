package com.ydzbinfo.emis.trainRepair.remotemodel.resume;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author 高晗
 * @description
 * @createDate 2021/9/17 11:09
 **/
public class HighLevelRepair {
    /**
     * 日期
     */
    private String date;

    /**
     * 高级修修程
     */
    private String gjxLevel;

    /**
     * 高级修单位
     */
    private String stsDeptName;

    /**
     * 车组id
     */
    private String trainsetid;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    @JSONField(name = "gjxlevel")
    public String getGjxLevel() {
        return gjxLevel;
    }

    public void setGjxLevel(String gjxLevel) {
        this.gjxLevel = gjxLevel;
    }

    public String getStsDeptName() {
        return stsDeptName;
    }
    @JSONField(name = "stsdeptname")
    public void setStsDeptName(String stsDeptName) {
        this.stsDeptName = stsDeptName;
    }

    public String getTrainsetid() {
        return trainsetid;
    }

    public void setTrainsetid(String trainsetid) {
        this.trainsetid = trainsetid;
    }
}
