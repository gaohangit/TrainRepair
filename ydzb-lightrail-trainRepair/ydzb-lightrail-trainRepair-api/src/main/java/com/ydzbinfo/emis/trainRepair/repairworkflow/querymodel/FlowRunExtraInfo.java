package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 流程运行额外信息表
 * </p>
 *
 * @author zhangtk
 * @since 2021-05-14
 */
@TableName("XZY_M_FLOWRUNEXTRAINFO")
@Data
public class FlowRunExtraInfo implements Serializable {

    /**
     * 流程运行表id
     */
    @TableField("S_FLOWRUNID")
    private String flowRunId;

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

}
