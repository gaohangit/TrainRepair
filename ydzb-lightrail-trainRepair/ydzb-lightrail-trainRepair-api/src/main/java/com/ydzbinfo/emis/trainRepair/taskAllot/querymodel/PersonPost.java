package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import java.io.Serializable;
import java.util.Date;
/**
 * <p>
 * 
 * </p>
 *
 * @author zhangtk
 * @since 2021-04-02
 */
@TableName("XZY_C_PERSONPOST")
public class PersonPost implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 岗位ID
     */
    @TableField("S_POSTID")
    private String postId;

    /**
     * 部门CODE
     */
    @TableField("S_DEPTCODE")
    private String deptCode;

    /**
     * 小组CODE
     */
    @TableField("S_BRANCHCODE")
    private String branchCode;

    /**
     * 职工ID
     */
    @TableField("S_STAFFID")
    private String staffId;

    /**
     * 职工姓名
     */
    @TableField("S_STAFFNAME")
    private String staffName;

    /**
     * 是否可用  1--可用 0--不可用
     */
    @TableField("C_FLAG")
    private String flag;

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
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String unitCode;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }
    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
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
    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
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
        return "Personpost{" +
            "id=" + id +
            ", postId=" + postId +
            ", deptCode=" + deptCode +
            ", branchCode=" + branchCode +
            ", staffId=" + staffId +
            ", staffName=" + staffName +
            ", flag=" + flag +
            ", createTime=" + createTime +
            ", createUserCode=" + createUserCode +
            ", createUserName=" + createUserName +
            ", unitCode=" + unitCode +
            ", delTime=" + delTime +
            ", delUserCode=" + delUserCode +
            ", delUserName=" + delUserName +
        "}";
    }
}
