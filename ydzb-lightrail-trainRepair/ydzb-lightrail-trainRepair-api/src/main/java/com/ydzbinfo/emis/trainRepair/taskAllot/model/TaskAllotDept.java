package com.ydzbinfo.emis.trainRepair.taskAllot.model;

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
 * @since 2021-06-16
 */
@TableName("XZY_M_TASKALLOTDEPT")
@Data
public class TaskAllotDept implements Serializable {


    /**
     * 主键
     */
    @TableId("S_TASKALLOTDEPTID")
    private String taskAllotDeptId;

    /**
     * 部门编码
     */
    @TableField("S_DEPTCODE")
    private String deptCode;

    /**
     * 部门名称
     */
    @TableField("S_DEPTNAME")
    private String deptName;

    /**
     * 派工唯一编码
     */
    @TableField("S_PUBLISHCODE")
    private String publishCode;
}
