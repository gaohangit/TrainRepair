package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 节点向量表
 * </p>
 *
 * @author 张天可
 * @since 2021-04-09
 */
@TableName("XZY_C_NODEVECTOR")
public class NodeVector implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 所属流程主键
     */
    @TableField("S_FLOWID")
    private String flowId;

    /**
     * 上游节点主键
     */
    @TableField("S_FROMNODEID")
    private String fromNodeId;

    /**
     * 下游节点主键
     */
    @TableField("S_TONODEID")
    private String toNodeId;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getFromNodeId() {
        return fromNodeId;
    }

    public void setFromNodeId(String fromNodeId) {
        this.fromNodeId = fromNodeId;
    }

    public String getToNodeId() {
        return toNodeId;
    }

    public void setToNodeId(String toNodeId) {
        this.toNodeId = toNodeId;
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
        return "NodeVector{" +
            "id=" + id +
            ", flowId=" + flowId +
            ", fromNodeId=" + fromNodeId +
            ", toNodeId=" + toNodeId +
            ", synFlag=" + synFlag +
            ", synDate=" + synDate +
        "}";
    }
}
