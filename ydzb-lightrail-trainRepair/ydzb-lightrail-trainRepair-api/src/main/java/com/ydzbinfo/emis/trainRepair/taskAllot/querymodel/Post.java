package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author 高晗
 * @since 2021-08-16
 */
@TableName("XZY_C_POST")
public class Post implements Serializable {


    /**
     * 岗位ID
     */
    @TableId("S_POSTID")
    private String postId;

    /**
     * 岗位名称
     */
    @TableField("S_POSTNAME")
    private String postName;

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

    @TableField("I_SORT")
    private String sort;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
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

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }


    @Override
    public String toString() {
        return "Post{" +
            "postId=" + postId +
            ", postName=" + postName +
            ", flag=" + flag +
            ", createTime=" + createTime +
            ", createUserCode=" + createUserCode +
            ", createUserName=" + createUserName +
            ", delTime=" + delTime +
            ", delUserCode=" + delUserCode +
            ", delUserName=" + delUserName +
            ", sort=" + sort +
        "}";
    }
}
