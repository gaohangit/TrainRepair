package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 流程额外信息表	如：是否为默认流程，流程选择聚合条件，切换人员聚合条件，触发人员聚合条件
 * </p>
 *
 * @author 张天可
 * @since 2021-04-02
 */
@TableName("XZY_C_FLOWEXTRAINFO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowExtraInfo implements Serializable {

    /**
     * 流程主键
     */
    @TableField("S_FLOWID")
    private String flowId;

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

}
