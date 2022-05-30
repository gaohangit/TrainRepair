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
 * @since 2021-03-01
 */
@TableName("XZY_C_MONITORPACKET")
public class MonitorPacket implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 车型
     */
    @TableField("S_TRAINTYPE")
    private String trainType;

    /**
     * 批次
     */
    @TableField("S_BATCHCODE")
    private String batchCode;

    /**
     * 作业包名称
     */
    @TableField("S_PACKETNAME")
    private String packetName;

    /**
     * 作业包CODE
     */
    @TableField("S_PACKETCODE")
    private String packetCode;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 创建人CODE
     */
    @TableField("S_CREATEUSERCODE")
    private String createUserCode;

    /**
     * 创建人名称
     */
    @TableField("S_CREATEUSERNAME")
    private String createUserName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getPacketName() {
        return packetName;
    }

    public void setPacketName(String packetName) {
        this.packetName = packetName;
    }

    public String getPacketCode() {
        return packetCode;
    }

    public void setPacketCode(String packetCode) {
        this.packetCode = packetCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(String createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    @Override
    public String toString() {
        return "MonitorPacket{" +
                "id='" + id + '\'' +
                ", trainType='" + trainType + '\'' +
                ", batchCode='" + batchCode + '\'' +
                ", packetName='" + packetName + '\'' +
                ", packetCode='" + packetCode + '\'' +
                ", createTime='" + createTime + '\'' +
                ", createUserCode='" + createUserCode + '\'' +
                ", createUserName='" + createUserName + '\'' +
                '}';
    }
}
