package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 节点业务类型表(比如某些节点要关联故障处理、故障质检)
 * </p>
 *
 * @author zhangtk
 * @since 2021-04-27
 */
@TableName("XZY_B_NODEFUNCTIONTYPE")
@Data
public class NodeFunctionType implements Serializable {


    /**
     * 业务类型标识
     */
    @TableId("S_CODE")
    private String code;

    /**
     * 业务类型名称
     */
    @TableField("S_NAME")
    private String name;

    /**
     * 流程类型标识
     */
    @TableField("S_FLOWTYPECODE")
    private String flowTypeCode;

    /**
     * 是否删除(1 已删除, 0 未删除)
     */
    @TableField("C_DELETED")
    private String deleted;

    /**
     * 删除时间
     */
    @TableField("D_DELETETIME")
    private Date deleteTime;

    /**
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String unitCode;

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

}
