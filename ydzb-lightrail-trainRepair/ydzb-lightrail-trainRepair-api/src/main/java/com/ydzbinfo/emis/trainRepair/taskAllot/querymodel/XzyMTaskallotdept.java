package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-11
 */
public class XzyMTaskallotdept extends Model<XzyMTaskallotdept> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String taskAllotDeptId;

    /**
     * 部门名称
     */
    private String deptName;


    /**
     * 部门编号
     */
    private String deptCode;

    public String getTaskAllotDeptId() {
        return taskAllotDeptId;
    }

    public void setTaskAllotDeptId(String taskAllotDeptId) {
        this.taskAllotDeptId = taskAllotDeptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    @Override
    public String toString() {
        return "XzyMTaskallotdept{" +
                "taskAllotDeptId='" + taskAllotDeptId + '\'' +
                ", deptName='" + deptName + '\'' +
                ", deptCode='" + deptCode + '\'' +
                '}';
    }

    @Override
    protected Serializable pkVal() {
        return this.taskAllotDeptId;
    }
}
