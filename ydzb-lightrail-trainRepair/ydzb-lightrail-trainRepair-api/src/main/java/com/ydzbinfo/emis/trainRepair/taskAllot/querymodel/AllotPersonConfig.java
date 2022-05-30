package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableField;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhangtk
 * @since 2021-03-23
 */
@TableName("XZY_C_ALLOTPERSON_CONFIG")
public class AllotPersonConfig implements Serializable {


    /**
     * 主键
     */
    @TableField("S_REPAIRPERSONALLOTID")
    private String repairPersonAllotId;

    /**
     * 部门CODE
     */
    @TableField("S_DEPTCODE")
    private String deptCode;

    /**
     * 部门名称
     */
    @TableField("S_DEPTNAME")
    private String deptName;

    /**
     * 班组CODE
     */
    @TableField("S_BRANCHCODE")
    private String branchCode;

    /**
     * 班组名称
     */
    @TableField("S_BRANCHNAME")
    private String branchName;

    /**
     * 作业人员CODE
     */
    @TableField("S_WORKCODE")
    private String workCode;

    /**
     * 作业人员名称
     */
    @TableField("S_WORKNAME")
    private String workName;

    /**
     * 是否兼职  1--是  0--否
     */
    @TableField("C_PARTTIME")
    private String partTime;

    /**
     * 是否可用  1--可用 0--不可用
     */
    @TableField("C_FLAG")
    private String flag;

    /**
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 排序ID
     */
    @TableField("S_SORT")
    private String sort;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 创建人
     */
    @TableField("S_CREATEUSERCODE")
    private String createUserCode;

    /**
     * 创建人名称
     */
    @TableField("S_CREATEUSERNAME")
    private String createUserName;

    /**
     * 删除时间
     */
    @TableField("D_DELTIME")
    private Date delTime;

    /**
     * 删除人
     */
    @TableField("S_DELUSERCODE")
    private String delUserCode;

    /**
     * 删除人名称
     */
    @TableField("S_DELUSERNAME")
    private String delUserName;

    public String getRepairPersonAllotId() {
        return repairPersonAllotId;
    }

    public void setRepairPersonAllotId(String repairPersonAllotId) {
        this.repairPersonAllotId = repairPersonAllotId;
    }
    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }
    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }
    public String getPartTime() {
        return partTime;
    }

    public void setPartTime(String partTime) {
        this.partTime = partTime;
    }
    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }
    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(String createUserCode) {
        this.createUserCode = createUserCode;
    }
    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
    public Date getDelTime() {
        return delTime;
    }

    public void setDelTime(Date delTime) {
        this.delTime = delTime;
    }
    public String getDelUserCode() {
        return delUserCode;
    }

    public void setDelUserCode(String delUserCode) {
        this.delUserCode = delUserCode;
    }
    public String getDelUserName() {
        return delUserName;
    }

    public void setDelUserName(String delUserName) {
        this.delUserName = delUserName;
    }

    @Override
    public String toString() {
        return "AllotpersonConfig{" +
            "repairPersonAllotId=" + repairPersonAllotId +
            ", deptCode=" + deptCode +
            ", deptName=" + deptName +
            ", branchCode=" + branchCode +
            ", branchName=" + branchName +
            ", workCode=" + workCode +
            ", workName=" + workName +
            ", partTime=" + partTime +
            ", flag=" + flag +
            ", unitCode=" + unitCode +
            ", sort=" + sort +
            ", createTime=" + createTime +
            ", createUserCode=" + createUserCode +
            ", createUserName=" + createUserName +
            ", delTime=" + delTime +
            ", delUserCode=" + delUserCode +
            ", delUserName=" + delUserName +
        "}";
    }
}
