package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.utils.entity.Constants;
import com.ydzbinfo.emis.utils.entity.ToStringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 流程基本信息
 * </p>
 *
 * @author 张天可
 * @since 2021-04-02
 */
@TableName("XZY_C_FLOW")
@Data
public class BaseFlow implements Serializable, IBaseFlow {

    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 流程名称
     */
    @TableField("S_NAME")
    private String name;

    /**
     * 流程类型标识
     */
    @TableField("S_FLOWTYPECODE")
    private String flowTypeCode;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    @JSONField(format = Constants.DEFAULT_DATE_TIME_FORMAT)
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
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 删除时间
     */
    @TableField("D_DELETETIME")
    @JSONField(format = Constants.DEFAULT_DATE_TIME_FORMAT)
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

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
