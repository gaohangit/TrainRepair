package com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("XZY_B_CRITERTIONROLE_DICT")
public class XzyBCritertionDict {

    /**
     * 预警角色CODE
     */
    @TableField("S_ROLECODE")
    private String roleCode;

    /**
     * 预警角色名称
     */
    @TableField("S_ROLENAME")
    private String roleName;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
