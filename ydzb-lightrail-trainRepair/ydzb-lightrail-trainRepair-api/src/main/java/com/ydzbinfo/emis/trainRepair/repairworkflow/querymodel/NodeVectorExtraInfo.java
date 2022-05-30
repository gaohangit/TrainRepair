package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.IExtraInfoBase;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 节点向量额外信息表	
 * </p>
 *
 * @author 张天可
 * @since 2021-04-09
 */
@TableName("XZY_C_NODEVECTOREXTRAINFO")
public class NodeVectorExtraInfo implements Serializable, IExtraInfoBase {


    /**
     * 节点向量主键
     */
    @TableField("S_NODEVECTORID")
    private String nodeVectorId;

    /**
     * 额外信息类型
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 额外信息值
     */
    @TableField("S_VALUE")
    private String value;

    /**
     * 同步标识 0未同步 1已同步
     */
    @TableField("C_SYNFLAG")
    private String synFlag;

    /**
     * 同步时间
     */
    @TableField("D_SYNDATE")
    private Date synDate;

    public String getNodeVectorId() {
        return nodeVectorId;
    }

    public void setNodeVectorId(String nodeVectorId) {
        this.nodeVectorId = nodeVectorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSynFlag() {
        return synFlag;
    }

    public void setSynFlag(String synFlag) {
        this.synFlag = synFlag;
    }

    public Date getSynDate() {
        return synDate;
    }

    public void setSynDate(Date synDate) {
        this.synDate = synDate;
    }


    @Override
    public String toString() {
        return "NodeVectorExtraInfo{" +
            "nodeVectorId=" + nodeVectorId +
            ", type=" + type +
            ", value=" + value +
            ", synFlag=" + synFlag +
            ", synDate=" + synDate +
        "}";
    }
}
