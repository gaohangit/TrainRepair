package com.ydzbinfo.emis.trainRepair.common.model;

import com.ydzbinfo.hussar.system.bsp.organ.SysOrgan;
import lombok.Data;

import java.util.List;

/**
 * @author 张天可
 * @description
 * @createDate 2021/4/19 17:41
 **/
@Data
public class User {
    // 用户data id
    private String id;

    // 用户name
    private String name;

    // 用户id
    private String staffId;

    // 用户登录ip地址
    private String ipAddress;

    // 用户 账户名称
    private String accountName;

    // 用户所属局
    private Bureau bureau;

    // 用户所属运用所
    private Unit unit;

    /**
     * 获取运用所信息通常使用ContextUtils.getUnitCode、ContextUtils.getUnitName
     */
    @Deprecated
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    // 用户所属班组
    private WorkTeam workTeam;

    /**
     * 兼职班组集合
     */
    private List<WorkTeam> partTimeWorkTeams;

    /**
     * 用户角色id集合
     */
    @Deprecated
    List<String> roles;

    /**
     * 角色信息列表
     */
    List<Role> roleInfoList;

    // 用户所属段
    private Depot depot;

    // 用户所属部门类型 (局段所)
    private String unitType;

    /**
     * 当前用户所在部门信息
     */
    private SysOrgan departmentOrgan;
}
