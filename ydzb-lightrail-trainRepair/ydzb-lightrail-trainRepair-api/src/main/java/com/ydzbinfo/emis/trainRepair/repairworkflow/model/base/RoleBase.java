package com.ydzbinfo.emis.trainRepair.repairworkflow.model.base;

import lombok.Data;

/**
 * 角色基本信息
 *
 * @author 张天可
 * @description
 * @createDate 2021/6/10 15:46
 **/
@Data
public class RoleBase {

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 角色类型  1-岗位角色  2-系统角色
     * {@code com.ydzbinfo.emis.trainRepair.constant.RoleTypeEnum}
     */
    private String type;
}
