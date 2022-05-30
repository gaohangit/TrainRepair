package com.ydzbinfo.emis.trainRepair.workProcessMonitor.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-24
 */
@TableName("XZY_C_PROCESS_USERCONFIG")
@Data
public class ProcessUserConfig implements Serializable {


    /**
     * 主键
     */
    @TableId("S_CONFIGID")
    private String configId;

    /**
     * 职工ID
     */
    @TableField("S_STAFFID")
    private String staffId;

    /**
     * 参数名称
     */
    @TableField("S_PARMNAME")
    private String parmName;

    /**
     * 参数值
     */
    @TableField("S_PARMVALUE")
    private String parmValue;

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
