package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhangtk
 * @since 2021-03-26
 */
@TableName("XZY_C_TASKALLOTSTYLE")
@Data
public class TaskAllotStyle implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 运用所
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 部门
     */
    @TableField("S_DEPTCODE")
    private String deptCode;

    /**
     * 模式   1--作业包   2--专业分工
     */
    @TableField("S_MODE")
    private String mode;

    /**
     * 记录时间
     */
    @TableField("S_RECORDTIME")
    private Date recordTime;

    /**
     * 记录人姓名
     */
    @TableField("S_RECORDERNAME")
    private String recorderName;

    /**
     * 记录人编码
     */
    @TableField("S_RECORDERCODE")
    private String recorderCode;
}
