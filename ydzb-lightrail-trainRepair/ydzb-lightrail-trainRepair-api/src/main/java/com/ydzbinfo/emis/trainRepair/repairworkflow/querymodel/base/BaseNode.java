package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 节点基础信息
 * </p>
 *
 * @author 张天可
 * @since 2021-04-02
 */
@Data
@TableName("XZY_C_NODE")
public class BaseNode implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 节点名称
     */
    @TableField("S_NAME")
    private String name;

    /**
     * 所属流程主键
     */
    @TableField("S_FLOWID")
    private String flowId;

    /**
     * 子流程主键
     */
    @TableField("S_CHILDFLOWID")
    private String childFlowId;

    /**
     * 备注信息
     */
    @TableField("S_REMARK")
    private String remark;

    @Override
    public String toString() {
        return "Node{" +
            "id=" + id +
            ", name=" + name +
            ", flowId=" + flowId +
            ", childFlowId=" + childFlowId +
            ", remark=" + remark +
        "}";
    }
}
