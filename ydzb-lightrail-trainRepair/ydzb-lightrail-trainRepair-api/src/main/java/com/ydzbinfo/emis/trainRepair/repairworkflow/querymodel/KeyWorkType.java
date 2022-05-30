package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 关键作业类型表
 * </p>
 *
 * @author 高晗
 * @since 2021-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_C_KEYWORKTYPE")
public class KeyWorkType implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 类型名称
     */
    @TableField("S_NAME")
    private String name;

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
     * 删除人ID
     */
    @TableField("S_DELETEWORKERID")
    private String deleteWorkerId;

    /**
     * 删除人名称
     */
    @TableField("S_DELETEWORKERNAME")
    private String deleteWorkerName;

    /**
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 修改时间
     */
    @TableField("D_UPDATETIME")
    private Date updateTime;

    /**
     * 修改人ID
     */
    @TableField("S_UPDATEWORKERID")
    private String updateWorkerId;

    /**
     * 修改人名称
     */
    @TableField("S_UPDATEWORKERNAME")
    private String updateWorkerName;

    @TableField("I_SORT")
    private Integer sort;


}
