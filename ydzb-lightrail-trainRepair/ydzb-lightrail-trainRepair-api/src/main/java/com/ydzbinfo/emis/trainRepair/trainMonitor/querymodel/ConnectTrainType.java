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
 * @since 2021-03-01
 */
@TableName("XZY_C_CONNECTTRAINTYPE")
public class ConnectTrainType implements Serializable {


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
     * 重联车型，用逗号区分
     */
    @TableField("S_CONNECTTRAINTYPE")
    private String connectTrainType;

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

    public String getConnectTrainType() {
        return connectTrainType;
    }

    public void setConnectTrainType(String connectTrainType) {
        this.connectTrainType = connectTrainType;
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
        return "ConnectTrainType{" +
                "id='" + id + '\'' +
                ", trainType='" + trainType + '\'' +
                ", connectTrainType='" + connectTrainType + '\'' +
                ", createTime='" + createTime + '\'' +
                ", createUserCode='" + createUserCode + '\'' +
                ", createUserName='" + createUserName + '\'' +
                '}';
    }
}
