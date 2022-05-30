package com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;

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
@TableName("XZY_M_TRAINSETPOSTION_HIS")
public class TrainsetPostionHis implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 历史主键
     */
    @TableField("S_TRAINSETPOSTIONID")
    private String trainsetPostionId;

    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    private String trainsetId;

    /**
     * 车组名称
     */
    @TableField("S_TRAINSETNAME")
    private String trainsetName;

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
     * 是否长编  1--是  0--否
     */
    @TableField("C_ISLONG")
    private String isLong;

    /**
     * 是否重联  1--是  0--否
     */
    @TableField("C_ISCONNECT")
    private String isConnect;

    /**
     * 进入时间
     */
    @TableField("D_INTIME")
    private Date inTime;


    /**
     * 离开时间
     */
    @TableField("D_OUTTIME")
    private Date outTime;

    /**
     * 数据来源
     */
    @TableField("S_DATASOURCE")
    private String dataSource;

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
     * 车头方向   01--车头在左侧   00--车头在右侧
     */
    @TableField("S_HEADDIRECTION")
    private String headDirection;

    /**
     * 车头所在列位
     */
    @TableField("S_HEADDIRECTIONPLA")
    private String headDirectionPla;

    /**
     * 车尾方向  01--车尾在左侧   00--车尾在右侧
     */
    @TableField("S_TAILDIRECTION")
    private String tailDirection;

    /**
     * 车尾所在列位
     */
    @TableField("S_TAILDIRECTIONPLA")
    private String tailDirectionPla;

    /**
     * 记录时间
     */
    @TableField("D_RECORDTIME")
    private Date recordTime;

    /**
     * 车头所在列位CODE
     */
    @TableField("S_HEADDIRECTIONPLACODE")
    private String headDirectionPlaCode;

    /**
     * 车尾所在列位 CODE
     */
    @TableField("S_TAILDIRECTIONPLACODE")
    private String tailDirectionPlaCode;

    /**
     * 操作人CODE
     */
    @TableField("S_RECORDUSERCODE")
    private String recordUserCode;

    /**
     * 操作人名称
     */
    @TableField("S_RECORDUSERNAME")
    private String recordUserName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTrainsetPostionId() {
        return trainsetPostionId;
    }

    public void setTrainsetPostionId(String trainsetPostionId) {
        this.trainsetPostionId = trainsetPostionId;
    }
    public String getTrainsetId() {
        return trainsetId;
    }

    public void setTrainsetId(String trainsetId) {
        this.trainsetId = trainsetId;
    }
    public String getTrainsetName() {
        return trainsetName;
    }

    public void setTrainsetName(String trainsetName) {
        this.trainsetName = trainsetName;
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
    public String getIsLong() {
        return isLong;
    }

    public void setIsLong(String isLong) {
        this.isLong = isLong;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }
    public Date getOutTime() {
        return outTime;
    }

    public void setOutTime(Date outTime) {
        this.outTime = outTime;
    }
    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
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
    public String getHeadDirection() {
        return headDirection;
    }

    public void setHeadDirection(String headDirection) {
        this.headDirection = headDirection;
    }
    public String getHeadDirectionPla() {
        return headDirectionPla;
    }

    public void setHeadDirectionPla(String headDirectionPla) {
        this.headDirectionPla = headDirectionPla;
    }
    public String getTailDirection() {
        return tailDirection;
    }

    public void setTailDirection(String tailDirection) {
        this.tailDirection = tailDirection;
    }
    public String getTailDirectionPla() {
        return tailDirectionPla;
    }

    public void setTailDirectionPla(String tailDirectionPla) {
        this.tailDirectionPla = tailDirectionPla;
    }
    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }
    public String getHeadDirectionPlaCode() {
        return headDirectionPlaCode;
    }

    public void setHeadDirectionPlaCode(String headDirectionPlaCode) {
        this.headDirectionPlaCode = headDirectionPlaCode;
    }
    public String getTailDirectionPlaCode() {
        return tailDirectionPlaCode;
    }

    public void setTailDirectionPlaCode(String tailDirectionPlaCode) {
        this.tailDirectionPlaCode = tailDirectionPlaCode;
    }
    public String getRecordUserCode() {
        return recordUserCode;
    }

    public void setRecordUserCode(String recordUserCode) {
        this.recordUserCode = recordUserCode;
    }
    public String getRecordUserName() {
        return recordUserName;
    }

    public void setRecordUserName(String recordUserName) {
        this.recordUserName = recordUserName;
    }

    public String getIsConnect() {
        return isConnect;
    }

    public void setIsConnect(String isConnect) {
        this.isConnect = isConnect;
    }

    @Override
    public String toString() {
        return "TrainsetpostionHis{" +
            "id=" + id +
            ", trainsetPostionId=" + trainsetPostionId +
            ", trainsetId=" + trainsetId +
            ", trainsetName=" + trainsetName +
            ", trackCode=" + trackCode +
            ", trackName=" + trackName +
            ", isLong=" + isLong +
            ", isconnect=" + isConnect +
            ", inTime=" + inTime +
            ", outTime=" + outTime +
            ", dataSource=" + dataSource +
            ", unitCode=" + unitCode +
            ", unitName=" + unitName +
            ", headDirection=" + headDirection +
            ", headDirectionPla=" + headDirectionPla +
            ", tailDirection=" + tailDirection +
            ", tailDirectionPla=" + tailDirectionPla +
            ", recordTime=" + recordTime +
            ", headDirectionPlaCode=" + headDirectionPlaCode +
            ", tailDirectionPlaCode=" + tailDirectionPlaCode +
            ", recordUserCode=" + recordUserCode +
            ", recordUserName=" + recordUserName +
        "}";
    }
}
