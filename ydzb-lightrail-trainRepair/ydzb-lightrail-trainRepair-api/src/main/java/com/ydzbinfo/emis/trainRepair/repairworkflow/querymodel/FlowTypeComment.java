package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 流程类型描述表
 * </p>
 *
 * @author zhangtk
 * @since 2021-05-08
 */
@TableName("XZY_B_FLOWTYPECOMMENT")
public class FlowTypeComment implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 流程类型标识
     */
    @TableField("S_FLOWTYPECODE")
    private String flowTypeCode;

    /**
     * 注解描述
     */
    @TableField("S_COMMENT")
    private String comment;

    /**
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlowTypeCode() {
        return flowTypeCode;
    }

    public void setFlowTypeCode(String flowTypeCode) {
        this.flowTypeCode = flowTypeCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }


    @Override
    public String toString() {
        return "FlowTypeComment{" +
            "id=" + id +
            ", flowTypeCode=" + flowTypeCode +
            ", comment=" + comment +
            ", unitCode=" + unitCode +
        "}";
    }
}
