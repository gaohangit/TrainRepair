package com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: 冯帅
 * @Date: 2021/4/23 10:04
 * @Description: 作业标准配置—优先角色
 */
@TableName("XZY_C_WORKCRITERTION_PRIROLE")
@Data
public class XzyCWorkritertionPriRole extends Model<XzyCWorkritertionPriRole> {

    private static final long serialVersionUID = 1L;

    @Override
    protected Serializable pkVal() {
        return null;
    }

    /**
     * 主键
     */
    @TableId("S_ID")
    private String Id;

    /**
     * XZY_C_WORKCRITERTION表主键
     */
    @TableField("S_CRITERTIONID")
    private String sCritertionid;

    /**
     * 优先角色编码
     */
    @TableField("S_PRIROLECODE")
    private String priRoleCode;

    /**
     * 优先角色名称
     */
    @TableField("S_PRIROLENAME")
    private String priRoleName;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 是否可用  1-可用  0-不可用
     */
    @TableField("C_FLAG")
    private String flag;

    /**
     * 类型  1-岗位  2-角色
     */
    @TableField("S_TYPE")
    private String type;
}
