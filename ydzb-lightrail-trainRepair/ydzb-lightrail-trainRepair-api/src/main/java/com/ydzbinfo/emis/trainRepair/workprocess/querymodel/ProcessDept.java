package com.ydzbinfo.emis.trainRepair.workprocess.querymodel;

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
 * @since 2021-05-08
 */
@TableName("XZY_M_PROCESSDEPT")
@Data
public class ProcessDept implements Serializable {


    /**
     * 主键
     */
    @TableId("S_PROCESSDEPTID")
    private String processDeptId;

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
    @Override
    public String toString() {
        return "ProcessDept{" +
            "processDeptId=" + processDeptId +
            ", deptCode=" + deptCode +
            ", deptName=" + deptName +
        "}";
    }
}
