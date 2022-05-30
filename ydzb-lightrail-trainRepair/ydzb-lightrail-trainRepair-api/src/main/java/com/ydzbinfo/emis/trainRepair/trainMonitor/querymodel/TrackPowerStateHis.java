package com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author gaohan
 * @since 2021-02-26
 */
@TableName("XZY_M_TRACKPOWER_STATE_HIS")
public class TrackPowerStateHis implements Serializable {


    /**
     * ID
     */
    @TableId("S_ID")
    private String id;

    /**
     * 股道CODE
     */
    @TableField("S_TRACKCODE")
    private String trackCode;

    /**
     * 股道名称
     */
    @TableField("S_TRACKNAME")
    private String trackName;

    /**
     * 列位NAME
     */
    @TableField("S_TRACKPLANAME")
    private String trackPlaName;

    /**
     * 列位CODE
     */
    @TableField("S_TRACKPLACODE")
    private String trackPlaCode;

    /**
     * 状态   1--有电  2--无电
     */
    @TableField("S_STATE")
    private String state;

    /**
     * 时间
     */
    @TableField("D_TIME")
    private Date time;

    /**
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 运用所名称
     */
    @TableField("S_UNITNAME")
    private String unitName;

    /**
     * 数据来源
     */
    @TableField("S_DATASOURCE")
    private String dataSource;

    /**
     * 记录时间
     */
    @TableField("D_RECORDTIME")
    private Date recordTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTrackCode() {
        return trackCode;
    }

    public void setTrackCode(String trackCode) {
        this.trackCode = trackCode;
    }
    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackPlaName() {
        return trackPlaName;
    }

    public void setTrackPlaName(String trackPlaName) {
        this.trackPlaName = trackPlaName;
    }

    public String getTrackPlaCode() {
        return trackPlaCode;
    }

    public void setTrackPlaCode(String trackPlaCode) {
        this.trackPlaCode = trackPlaCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }
    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public String toString() {
        return "TrackpowerStateHis{" +
            "id=" + id +
            ", trackCode=" + trackCode +
            ", trackName=" + trackName +
            ", trackplaname=" + trackPlaName +
            ", trackplacode=" + trackPlaCode +
            ", state=" + state +
            ", time=" + time +
            ", unitCode=" + unitCode +
            ", unitName=" + unitName +
            ", dataSource=" + dataSource +
            ", recordTime=" + recordTime +
        "}";
    }
}
