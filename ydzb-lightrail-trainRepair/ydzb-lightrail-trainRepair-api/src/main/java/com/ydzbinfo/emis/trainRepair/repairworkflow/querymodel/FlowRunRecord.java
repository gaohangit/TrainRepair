package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.utils.entity.Constants;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 流程运行记录表
 * </p>
 *
 * @author zhangtk
 * @since 2021-05-13
 */
@TableName("XZY_M_FLOWRUNRECORD")
@Data
public class FlowRunRecord implements Serializable {

    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 流程运行表主键
     */
    @TableField("S_FLOWRUNID")
    private String flowRunId;

    /**
     * 记录类型(FORCE_END 强制结束)
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 备注信息
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 记录时间
     */
    @TableField("D_RECORDTIME")
    @JSONField(format = Constants.DEFAULT_DATE_TIME_FORMAT)
    private Date recordTime;

    /**
     * 记录者id
     */
    @TableField("S_WORKERID")
    private String workerId;

    /**
     * 记录者名称
     */
    @TableField("S_WORKERNAME")
    private String workerName;

}
