package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 关键作业大屏配置
 * </p>
 *
 * @author 张天可
 * @since 2021-08-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_C_KEYWORKMONITORCONFIG")
public class KeyWorkMonitorConfig implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 职工ID
     */
    @TableField("S_STAFFID")
    private String staffId;

    /**
     * 参数名称
     */
    @TableField("S_PARAMNAME")
    private String paramName;

    /**
     * 参数值
     */
    @TableField("S_PARAMVALUE")
    private String paramValue;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 排序ID
     */
    @TableField("I_SORTID")
    private Integer sortId;

    /**
     * 运用所CODE
     */
    @TableField("S_UNITCODE")
    private String unitCode;


}
