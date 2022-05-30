package com.ydzbinfo.emis.trainRepair.common.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

/**
 * <p>
 * 日志表
 * </p>
 *
 * @author 高晗
 * @since 2021-09-17
 */
@TableName("XZY_M_LOG")
public class Log implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 1--用户类  2--系统类
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 模块名称
     */
    @TableField("S_MODULE")
    private String module;

    /**
     * 日志内容
     */
    @TableField("S_CONTENT")
    private String content;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 使用人名称
     */
    @TableField("S_CREATESTAFFNAME")
    private String createStaffName;

    /**
     * 使用人ID
     */
    @TableField("S_CREATESTAFFID")
    private String createStaffId;

    /**
     * 地址IP
     */
    @TableField("S_ADDRESSIP")
    private String addressIp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateStaffName() {
        return createStaffName;
    }

    public void setCreateStaffName(String createStaffName) {
        this.createStaffName = createStaffName;
    }

    public String getCreateStaffId() {
        return createStaffId;
    }

    public void setCreateStaffId(String createStaffId) {
        this.createStaffId = createStaffId;
    }

    public String getAddressIp() {
        return addressIp;
    }

    public void setAddressIp(String addressIp) {
        this.addressIp = addressIp;
    }


    @Override
    public String toString() {
        return "Log{" +
            "id=" + id +
            ", type=" + type +
            ", module=" + module +
            ", content=" + content +
            ", createTime=" + createTime +
            ", createStaffName=" + createStaffName +
            ", createStaffId=" + createStaffId +
            ", addressIp=" + addressIp +
        "}";
    }
}
