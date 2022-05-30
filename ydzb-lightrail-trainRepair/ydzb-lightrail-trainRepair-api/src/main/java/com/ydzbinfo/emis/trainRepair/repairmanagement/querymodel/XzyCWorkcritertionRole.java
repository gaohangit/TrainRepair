package com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

/**
 * <p>
 * 作业标准配置—预警角色
 * </p>
 *
 * @author 
 * @since 2020-07-21
 */
@TableName("XZY_C_WORKCRITERTION_ROLE")
public class XzyCWorkcritertionRole extends Model<XzyCWorkcritertionRole> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("S_ID")
    private String sId;
    /**
     * XZY_C_WORKCRITERTION表主键
     */
    @TableField("S_CRITERTIONID")
    private String sCritertionid;
    /**
     * 角色名称
     */
    @TableField("S_ROLENAME")
    private String sRolename;
    /**
     * 角色CODE
     */
    @TableField("S_ROLECODE")
    private String sRolecode;
    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String sRemark;

    /**
     * 是否可用 1-可用 0-不可用
     */
    @TableField("C_FLAG")
    private String cFlag;


    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getsCritertionid() {
        return sCritertionid;
    }

    public void setsCritertionid(String sCritertionid) {
        this.sCritertionid = sCritertionid;
    }

    public String getsRolename() {
        return sRolename;
    }

    public void setsRolename(String sRolename) {
        this.sRolename = sRolename;
    }

    public String getsRolecode() {
        return sRolecode;
    }

    public void setsRolecode(String sRolecode) {
        this.sRolecode = sRolecode;
    }

    public String getsRemark() {
        return sRemark;
    }

    public void setsRemark(String sRemark) {
        this.sRemark = sRemark;
    }

    public String getcFlag() {
        return cFlag;
    }

    public void setcFlag(String cFlag) {
        this.cFlag = cFlag;
    }

    @Override
    protected Serializable pkVal() {
        return this.sId;
    }

    @Override
    public String toString() {
        return "XzyCWorkcritertionRole{" +
                "sId='" + sId + '\'' +
                ", sCritertionid='" + sCritertionid + '\'' +
                ", sRolename='" + sRolename + '\'' +
                ", sRolecode='" + sRolecode + '\'' +
                ", sRemark='" + sRemark + '\'' +
                ", cFlag='" + cFlag + '\'' +
                '}';
    }
}
