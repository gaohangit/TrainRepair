package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 关键作业配置表
 * </p>
 *
 * @author 高晗
 * @since 2021-06-23
 */
@TableName("XZY_C_KEYWORKCONFIG")
public class KeyWorkConfig implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 作业内容
     */
    @TableField("S_CONTENT")
    private String content;

    /**
     * 车型
     */
    @TableField("S_TRAIN_MODEL")
    private String trainModel;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 创建人ID
     */
    @TableField("S_CREATEWORKERID")
    private String createWorkerId;

    /**
     * 创建人名称
     */
    @TableField("S_CREATEWORKERNAME")
    private String createWorkerName;

    /**
     * 运用所名称
     */
    @TableField("S_UNITNAME")
    private String unitName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTrainModel() {
        return trainModel;
    }

    public void setTrainModel(String trainModel) {
        this.trainModel = trainModel;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateWorkerId() {
        return createWorkerId;
    }

    public void setCreateWorkerId(String createWorkerId) {
        this.createWorkerId = createWorkerId;
    }

    public String getCreateWorkerName() {
        return createWorkerName;
    }

    public void setCreateWorkerName(String createWorkerName) {
        this.createWorkerName = createWorkerName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }


    @Override
    public String toString() {
        return "KeyWorkConfig{" +
            "id=" + id +
            ", unitCode=" + unitCode +
            ", content=" + content +
            ", trainModel=" + trainModel +
            ", createTime=" + createTime +
            ", createWorkerId=" + createWorkerId +
            ", createWorkerName=" + createWorkerName +
            ", unitName=" + unitName +
        "}";
    }
}
